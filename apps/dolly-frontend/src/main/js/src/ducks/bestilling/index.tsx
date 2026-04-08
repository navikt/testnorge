import { createActions } from 'redux-actions'
import { DollyApi } from '@/service/Api'
import { handleActions } from '@/ducks/utils/immerHandleActions'
import { getLeggTilIdent, rootPaths } from '@/components/bestillingsveileder/utils'
import { v4 as uuid } from 'uuid'
import * as _ from 'lodash-es'
import { Logger } from '@/logger/Logger'
import { appendDocumentChunk, initDocumentUpload } from '@/api'

const CHUNK_SIZE = 500_000

export const actions = createActions(
	{
		postBestillingLeggTilPaaPerson: DollyApi.createBestillingLeggTilPaaPerson,
		postBestillingFraEksisterendeIdenter: DollyApi.createBestillingFraEksisterendeIdenter,
		postBestillingLeggTilPaaGruppe: DollyApi.createBestillingLeggTilPaaGruppe,
		postBestilling: DollyApi.createBestilling,
		postOrganisasjonBestilling: DollyApi.createOrganisasjonBestilling,
		postTestnorgeBestilling: DollyApi.importerPersonerFraPdl,
		bestillingFeilet: (error) => ({ error }),
	},
	{ prefix: 'bestveil' },
)

const initialState = {
	error: null,
}

export default handleActions(
	{
		[actions.bestillingFeilet](state, action) {
			state.error = action.payload.error
		},
	},
	initialState,
)

export interface SendBestillingOptions {
	is: { [key: string]: boolean }
	personFoerLeggTil?: any
	identMaster?: string
	importPersoner?: { ident: string }[]
}

const trackBestilling = (values: Record<string, any>) => {
	const _uuid = uuid()
	Object.keys(values)
		.filter((key) => rootPaths.find((value) => value === key))
		.forEach((key) => {
			Logger.trace({ event: 'Bestilling av omraade', message: key, uuid: _uuid })
		})
}

const uploadSingleDocument = async (
	base64Data: string,
	dokumentType: string = 'BESTILLING_DOKARKIV',
): Promise<number> => {
	const dokumentId = await initDocumentUpload(dokumentType)

	for (let i = 0; i < base64Data.length; i += CHUNK_SIZE) {
		const chunk = base64Data.slice(i, i + CHUNK_SIZE)
		await appendDocumentChunk(dokumentId, chunk)
	}

	return dokumentId
}

const uploadDokarkivDocuments = async (values: any): Promise<any> => {
	if (!values?.dokarkiv?.length) {
		return values
	}

	const updatedDokarkiv = await Promise.all(
		values.dokarkiv.map(async (item: any) => {
			if (!item?.dokumenter?.length) {
				return item
			}

			const updatedDokumenter = await Promise.all(
				item.dokumenter.map(async (dok: any) => {
					if (!dok?.dokumentvarianter?.length) {
						return dok
					}

					const updatedVarianter = await Promise.all(
						dok.dokumentvarianter.map(async (variant: any) => {
							if (!variant.fysiskDokument) {
								return variant
							}

							const dokumentId = await uploadSingleDocument(
								variant.fysiskDokument,
								'BESTILLING_DOKARKIV',
							)
							return {
								...variant,
								fysiskDokument: undefined,
								dokumentReferanse: dokumentId,
							}
						}),
					)
					return { ...dok, dokumentvarianter: updatedVarianter }
				}),
			)
			return { ...item, dokumenter: updatedDokumenter }
		}),
	)

	return { ...values, dokarkiv: updatedDokarkiv }
}

const uploadHistarkDocuments = async (values: any): Promise<any> => {
	if (!values?.histark?.dokumenter?.length) {
		return values
	}

	const updatedDokumenter = await Promise.all(
		values.histark.dokumenter.map(async (dok: any) => {
			if (!dok.fysiskDokument) {
				return dok
			}

			const dokumentId = await uploadSingleDocument(dok.fysiskDokument, 'BESTILLING_HISTARK')
			return {
				...dok,
				fysiskDokument: undefined,
				dokumentReferanse: dokumentId,
			}
		}),
	)

	return {
		...values,
		histark: { ...values.histark, dokumenter: updatedDokumenter },
	}
}

const cleanBestillingValues = (values: any): any => {
	let cleaned = values

	if (cleaned?.dokarkiv?.length) {
		cleaned = {
			...cleaned,
			dokarkiv: cleaned.dokarkiv.map((item: any) => {
				const { vedlegg, skjema, skjemaValg, ...rest } = item
				return rest
			}),
		}
	}

	if (cleaned?.histark?.dokumenter?.length) {
		cleaned = {
			...cleaned,
			histark: {
				...cleaned.histark,
				dokumenter: cleaned.histark.dokumenter.map((dok: any) => {
					const { vedlegg, ...rest } = dok
					return rest
				}),
			},
		}
	}

	return cleaned
}

/**
 * Sender de ulike bestillingstypene fra Bestillingsveileder
 */
export const sendBestilling =
	(values: any, options: SendBestillingOptions, gruppeId: any, navigate: (url: string) => void) =>
	async (dispatch: any) => {
		const opts = options
		const valgtGruppe = values?.gruppeId || gruppeId
		const cleanedValues = cleanBestillingValues(values)
		const uploadedValues = await uploadHistarkDocuments(
			await uploadDokarkivDocuments(cleanedValues),
		)
		let bestillingAction

		if (opts.is.leggTil) {
			const ident = getLeggTilIdent(opts.personFoerLeggTil, opts.identMaster)
			bestillingAction = actions.postBestillingLeggTilPaaPerson(ident, uploadedValues)
		} else if (opts.is.leggTilPaaGruppe) {
			bestillingAction = actions.postBestillingLeggTilPaaGruppe(valgtGruppe, uploadedValues)
		} else if (opts.is.opprettFraIdenter) {
			bestillingAction = actions.postBestillingFraEksisterendeIdenter(valgtGruppe, uploadedValues)
		} else if (opts.is.importTestnorge) {
			const importValues = Object.assign({}, uploadedValues, {
				identer: (options.importPersoner || []).map((person: { ident: string }) => person.ident),
				environments: uploadedValues.environments || [],
			})
			bestillingAction = actions.postTestnorgeBestilling(importValues.valgtGruppe, importValues)
		} else if (uploadedValues.organisasjon) {
			trackBestilling(uploadedValues)
			bestillingAction = actions.postOrganisasjonBestilling(uploadedValues)
		} else {
			trackBestilling(uploadedValues)
			bestillingAction = actions.postBestilling(valgtGruppe, uploadedValues)
		}

		const response = await dispatch(bestillingAction)

		//IF ALL IS GOOD - REDIRECT
		const res = _.get(response, 'action.payload.data', null)
		const type = _.get(response, 'action.type', null)
		if (res.error) {
			dispatch(actions.bestillingFeilet(res))
		} else if (type.includes('OrganisasjonBestilling')) {
			navigate(`/organisasjoner`)
		} else if (opts.is.importTestnorge) {
			navigate(`/gruppe/${valgtGruppe}`)
		} else {
			navigate(`/gruppe/${valgtGruppe}`)
		}
	}
