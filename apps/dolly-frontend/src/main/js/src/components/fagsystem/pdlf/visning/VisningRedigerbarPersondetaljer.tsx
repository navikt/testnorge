import React, { useCallback, useRef, useState } from 'react'
import * as Yup from 'yup'
import Loading from '~/components/ui/loading/Loading'
import { Formik, FormikProps } from 'formik'
import { FoedselForm } from '~/components/fagsystem/pdlf/form/partials/foedsel/Foedsel'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import styled from 'styled-components'
import Button from '~/components/ui/button/Button'
import _get from 'lodash/get'
import { DollyApi, PdlforvalterApi } from '~/service/Api'
import Icon from '~/components/ui/icon/Icon'
import DollyModal from '~/components/ui/modal/DollyModal'
import useBoolean from '~/utils/hooks/useBoolean'
import { StatsborgerskapForm } from '~/components/fagsystem/pdlf/form/partials/statsborgerskap/Statsborgerskap'
import { DoedsfallForm } from '~/components/fagsystem/pdlf/form/partials/doedsfall/Doedsfall'
import {
	doedsfall,
	innflytting,
	statsborgerskap,
	utflytting,
} from '~/components/fagsystem/pdlf/form/validation'
import { ifPresent } from '~/utils/YupValidations'
import { InnvandringForm } from '~/components/fagsystem/pdlf/form/partials/innvandring/Innvandring'
import { UtvandringForm } from '~/components/fagsystem/pdlf/form/partials/utvandring/Utvandring'
import { PersondetaljerSamlet } from '~/components/fagsystem/pdlf/form/partials/persondetaljerSamlet/PersondetaljerSamlet'
import { Checkbox, DollyCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import _has from 'lodash/has'
import { isEqual } from 'lodash'

type VisningTypes = {
	getPdlForvalter: Function
	dataVisning: any
	initialValues: any
	redigertAttributt?: any
	path: string
	ident: string
}

enum Modus {
	Les = 'LES',
	Skriv = 'SKRIV',
	LoadingPdlf = 'LOADING_PDLF',
	LoadingPdl = 'LOADING_PDL',
}

const FieldArrayEdit = styled.div`
	&&& {
		button {
			position: relative;
			top: 0;
			right: 0;
			margin-right: 10px;
		}
	}
`

const PersondetaljerVisning = styled.div`
	width: 100%;
	position: relative;
`

const EditDeleteKnapper = styled.div`
	position: absolute;
	right: 0;
	top: 0;
	margin: -5px 10px 0 0;
	&&& {
		button {
			position: relative;
		}
	}
`

const Knappegruppe = styled.div`
	margin: 10px 0 5px 0;
	align-content: baseline;
`

const SlettCheckbox = styled(Checkbox)`
	margin-right: 20px;
`

export const VisningRedigerbarPersondetaljer = ({
	getPdlForvalter,
	dataVisning,
	initialValues,
	redigertAttributt = null,
	path,
	ident,
}: VisningTypes) => {
	const [visningModus, setVisningModus] = useState(Modus.Les)
	const [errorMessagePdlf, setErrorMessagePdlf] = useState(null)
	const [errorMessagePdl, setErrorMessagePdl] = useState(null)
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)

	// console.log('initialValues: ', initialValues) //TODO - SLETT MEG
	// console.log('redigertAttributt: ', redigertAttributt) //TODO - SLETT MEG

	const pdlfError = (error: any) => {
		error &&
			setErrorMessagePdlf(
				`Feil ved oppdatering i PDL-forvalter: ${error.message || error.toString()}`
			)
		setVisningModus(Modus.Les)
	}

	const pdlError = (error: any) => {
		error && setErrorMessagePdl(`Feil ved oppdatering i PDL: ${error.message || error.toString()}`)
		setVisningModus(Modus.Les)
	}

	const mountedRef = useRef(true)

	const handleSubmit = useCallback((data: any) => {
		const submit = async () => {
			setVisningModus(Modus.LoadingPdlf)
			const editFn = async () => {
				for (let attr of Object.keys(data)) {
					const initialData = redigertAttributt
						? _get(redigertAttributt, `${attr}[0]`)
						: _get(initialValues, `${attr}[0]`)
					const itemData = _get(data, `${attr}[0]`)
					console.log('initialData: ', initialData) //TODO - SLETT MEG
					console.log('itemData: ', itemData) //TODO - SLETT MEG
					console.log('redigertAttributt: ', redigertAttributt) //TODO - SLETT MEG
					if (!isEqual(itemData, initialData)) {
						await PdlforvalterApi.putAttributt(ident, attr, itemData?.id || 0, itemData).catch(
							(error) => {
								pdlfError(error)
							}
						)
					}
				}
			}
			return editFn()
				.then(() => {
					setVisningModus(Modus.LoadingPdl)
					DollyApi.sendOrdre(ident).then(() => {
						getPdlForvalter().then(() => {
							if (mountedRef.current) {
								setVisningModus(Modus.Les)
							}
						})
					})
				})
				.catch((error) => {
					pdlError(error)
				})
		}
		mountedRef.current = false
		return submit()
	}, [])

	const handleDelete = useCallback((slettAttr) => {
		const slett = async () => {
			setVisningModus(Modus.LoadingPdlf)
			const deleteFn = async () => {
				for (let attr of Object.keys(slettAttr)) {
					if (slettAttr[attr]) {
						const id = _get(initialValues, `${attr}[0].id`)
						await PdlforvalterApi.deleteAttributt(ident, attr, id).catch((error) => {
							pdlfError(error)
						})
					}
				}
			}
			return deleteFn()
				.then(() => {
					setVisningModus(Modus.LoadingPdl)
					DollyApi.sendOrdre(ident).then(() => {
						getPdlForvalter().then(() => {
							if (mountedRef.current) {
								setVisningModus(Modus.Les)
							}
						})
					})
				})
				.catch((error) => {
					pdlError(error)
				})
		}
		mountedRef.current = false
		return slett()
	}, [])

	const validationSchema = Yup.object().shape(
		{
			doedsfall: ifPresent('doedsfall', doedsfall),
			statsborgerskap: ifPresent('statsborgerskap', statsborgerskap),
			innflytting: ifPresent('innflytting', innflytting),
			utflytting: ifPresent('utflytting', utflytting),
		},
		[
			['doedsfall', 'doedsfall'],
			['statsborgerskap', 'statsborgerskap'],
			['innflytting', 'innflytting'],
			['utflytting', 'utflytting'],
		]
	)

	const harNavn = redigertAttributt
		? redigertAttributt?.navn?.[0]?.fornavn ||
		  redigertAttributt?.navn?.[0]?.mellomnavn ||
		  redigertAttributt?.navn?.[0]?.etternavn
		: initialValues?.navn?.[0]?.fornavn ||
		  initialValues?.navn?.[0]?.mellomnavn ||
		  initialValues?.navn?.[0]?.etternavn

	const harKjoenn = redigertAttributt
		? redigertAttributt?.kjoenn?.[0]?.kjoenn
		: initialValues?.kjoenn?.[0]?.kjoenn

	const harPersonstatus = redigertAttributt
		? redigertAttributt?.folkeregisterpersonstatus?.[0]?.status
		: initialValues?.folkeregisterpersonstatus?.[0]?.status

	const SlettModal = () => {
		const slettAttr = {
			navn: false,
			kjoenn: false,
			folkeregisterpersonstatus: false,
		}

		return (
			<DollyModal isOpen={modalIsOpen} closeModal={closeModal} width="40%" overflow="auto">
				<div className="slettModal">
					<div className="slettModal slettModal-content">
						<Icon size={50} kind="report-problem-circle" />
						<h1>Sletting</h1>
						<h4>Hvilke opplysninger ønsker du å slette?</h4>
						<div className="flexbox--flex-wrap">
							{harNavn && (
								<SlettCheckbox
									id={'navn'}
									label={'Navn'}
									size={'xxsmall'}
									onChange={() => {
										slettAttr.navn = !slettAttr.navn
									}}
								/>
							)}
							{harKjoenn && (
								<SlettCheckbox
									id={'kjoenn'}
									label={'Kjønn'}
									size={'xxsmall'}
									onChange={() => {
										slettAttr.kjoenn = !slettAttr.kjoenn
									}}
								/>
							)}
							{harPersonstatus && (
								<Checkbox
									id={'folkeregisterpersonstatus'}
									label={'Personstatus'}
									size={'xxsmall'}
									onChange={() => {
										slettAttr.folkeregisterpersonstatus = !slettAttr.folkeregisterpersonstatus
									}}
								/>
							)}
						</div>
					</div>
					<div className="slettModal-actions">
						<NavButton onClick={closeModal}>Avbryt</NavButton>
						<NavButton
							onClick={() => {
								closeModal()
								return handleDelete(slettAttr)
							}}
							type="hoved"
						>
							Slett
						</NavButton>
					</div>
				</div>
			</DollyModal>
		)
	}

	return (
		<>
			{visningModus === Modus.LoadingPdlf && <Loading label="Oppdaterer PDL-forvalter..." />}
			{visningModus === Modus.LoadingPdl && <Loading label="Oppdaterer PDL..." />}
			{visningModus === Modus.Les && (
				<PersondetaljerVisning>
					{dataVisning}
					<EditDeleteKnapper>
						<Button kind="edit" onClick={() => setVisningModus(Modus.Skriv)} title="Endre" />
						{(harNavn || harKjoenn || harPersonstatus) && (
							<Button kind="trashcan" onClick={() => openModal()} title="Slett" />
						)}
						<SlettModal />
					</EditDeleteKnapper>
					<div className="flexbox--full-width">
						{errorMessagePdlf && <div className="error-message">{errorMessagePdlf}</div>}
						{errorMessagePdl && <div className="error-message">{errorMessagePdl}</div>}
					</div>
				</PersondetaljerVisning>
			)}
			{visningModus === Modus.Skriv && (
				<Formik
					initialValues={redigertAttributt ? redigertAttributt : initialValues}
					onSubmit={handleSubmit}
					enableReinitialize
					validationSchema={validationSchema}
				>
					{(formikBag) => {
						// console.log('formikBag.values: ', formikBag.values) //TODO - SLETT MEG
						// console.log('formikBag.errors: ', formikBag.errors) //TODO - SLETT MEG
						return (
							<>
								<FieldArrayEdit>
									<div className="flexbox--flex-wrap">
										<PersondetaljerSamlet path={path} formikBag={formikBag} />
									</div>
									<Knappegruppe>
										<NavButton
											type="standard"
											htmlType="reset"
											onClick={() => setVisningModus(Modus.Les)}
											disabled={formikBag.isSubmitting}
											style={{ top: '1.75px' }}
										>
											Avbryt
										</NavButton>
										<NavButton
											type="hoved"
											htmlType="submit"
											onClick={() => formikBag.handleSubmit()}
											disabled={!formikBag.isValid || formikBag.isSubmitting}
										>
											Endre
										</NavButton>
									</Knappegruppe>
								</FieldArrayEdit>
							</>
						)
					}}
				</Formik>
			)}
		</>
	)
}
