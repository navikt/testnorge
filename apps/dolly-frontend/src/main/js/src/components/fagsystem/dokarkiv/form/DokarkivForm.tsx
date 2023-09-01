import React, { BaseSyntheticEvent, useEffect, useState } from 'react'
import * as Yup from 'yup'
import { ifPresent, requiredString } from '@/utils/YupValidations'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { DollyTextInput, FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { FormikProps } from 'formik'
import * as _ from 'lodash-es'
import { FormikCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'

const Digitalinnsending = React.lazy(
	() => import('@/components/fagsystem/dokarkiv/form/partials/Digitalinnsending'),
)

const FileUploader = React.lazy(() => import('@/utils/FileUploader/FileUploader'))

const DokumentInfoListe = React.lazy(
	() => import('@/components/fagsystem/dokarkiv/modal/DokumentInfoListe'),
)

interface DokarkivFormProps {
	formikBag: FormikProps<{}>
}

type Skjema = {
	data: string
	label: string
	lowercaseLabel: string
	value: string
}

export type Vedlegg = {
	id: string
	name: string
	dokNavn: string
	mimetype: string
	size: number
	content: {
		base64: string
	}
}

enum Kodeverk {
	TEMA = 'Tema',
	NAVSKJEMA = 'NAVSkjema',
}

export const dokarkivAttributt = 'dokarkiv'

export const DokarkivForm = ({ formikBag }: DokarkivFormProps) => {
	if (!_.has(formikBag.values, dokarkivAttributt)) {
		return null
	}

	const sessionDokumenter = _.get(formikBag.values, 'dokarkiv.vedlegg')
	const digitalInnsending = _.get(formikBag.values, 'dokarkiv.avsenderMottaker')
	const [files, setFiles] = useState(sessionDokumenter || [])
	const [skjemaValues, setSkjemaValues] = useState(_.get(formikBag.values, 'dokarkiv.skjema'))

	useEffect(() => {
		handleSkjemaChange(skjemaValues)
		handleVedleggChange(files)
	}, [files, skjemaValues])

	const handleSkjemaChange = (skjema: Skjema) => {
		if (!skjema) {
			return
		}

		setSkjemaValues(skjema)
		formikBag.setFieldValue('dokarkiv.tittel', skjema.data)
		formikBag.setFieldValue('dokarkiv.skjema', skjema)

		const dokumentVarianter = files.map((vedl: Vedlegg, index: number) => ({
			tittel: vedl.dokNavn ? vedl.dokNavn : vedl.name,
			brevkode: (index === 0 && skjema?.value) || undefined,
			dokumentvarianter: [
				{
					filtype: 'PDFA',
					fysiskDokument: vedl.content.base64,
					variantformat: 'ARKIV',
				},
			],
		}))

		dokumentVarianter.length > 0
			? formikBag.setFieldValue('dokarkiv.dokumenter', dokumentVarianter)
			: formikBag.setFieldValue('dokarkiv.dokumenter[0].tittel', skjema.data)
	}

	const handleVedleggChange = (filer: [Vedlegg]) => {
		setFiles(filer)
		formikBag.setFieldValue('dokarkiv.vedlegg', filer)
	}

	const handleSakstypeChange = (target) => {
		formikBag.setFieldValue('dokarkiv.sak.sakstype', target.value)
		if (target.value !== 'FAGSAK') {
			formikBag.setFieldValue('dokarkiv.sak.fagsaksystem', '')
			formikBag.setFieldValue('dokarkiv.sak.fagsakId', '')
		}
	}

	const harFagsak = _.get(formikBag.values, 'dokarkiv.sak.sakstype') === 'FAGSAK'

	return (
		// @ts-ignore
		<Vis attributt={dokarkivAttributt}>
			<Panel
				heading="Dokumenter (Joark)"
				hasErrors={panelError(formikBag, dokarkivAttributt)}
				iconType="designsystem-dokarkiv"
				// @ts-ignore
				startOpen={erForsteEllerTest(formikBag.values, [dokarkivAttributt])}
			>
				<Kategori
					title={`Oppretting av ${digitalInnsending ? 'digitalt' : 'skannet '} dokument`}
					vis={dokarkivAttributt}
				>
					<div className="flexbox--full-width">
						<FormikSelect
							name="dokarkiv.dokumenter[0].brevkode"
							label="Skjema"
							afterChange={handleSkjemaChange}
							kodeverk={Kodeverk.NAVSKJEMA}
							size="grow"
							optionHeight={50}
							isClearable={false}
						/>
					</div>
					<div className="flexbox--flex-wrap">
						<FormikSelect
							name="dokarkiv.tema"
							label="Tema"
							kodeverk={Kodeverk.TEMA}
							size="xlarge"
							isClearable={false}
						/>
						<DollyTextInput
							onChange={(event: BaseSyntheticEvent) => {
								formikBag.setFieldValue(
									'dokarkiv.journalfoerendeEnhet',
									event.target.value === '' ? undefined : event.target.value,
								)
							}}
							feil={
								_.get(formikBag.errors, `dokarkiv.journalfoerendeEnhet`)
									? { feilmelding: _.get(formikBag.errors, `dokarkiv.journalfoerendeEnhet`) }
									: null
							}
							name="dokarkiv.journalfoerendeEnhet"
							label="Journalførende enhet"
							size="large"
						/>
						<FormikSelect
							name="dokarkiv.sak.sakstype"
							label="Sakstype"
							options={Options('sakstype')}
							onChange={handleSakstypeChange}
							isClearable={false}
						/>
						{harFagsak && (
							<>
								<FormikSelect
									name="dokarkiv.sak.fagsaksystem"
									label="Fagsaksystem"
									options={Options('fagsaksystem')}
									size="large"
								/>
								<FormikTextInput name="dokarkiv.sak.fagsakId" label="Fagsak-ID" />
							</>
						)}
					</div>
					<FormikCheckbox name={`dokarkiv.ferdigstill`} label="Ferdigstill journalpost" />
					{digitalInnsending ? <Digitalinnsending /> : null}
					<Kategori title={'Vedlegg'}>
						<FileUploader files={files} setFiles={setFiles} />
						{files.length > 0 && (
							<DokumentInfoListe handleChange={handleVedleggChange} filer={files} />
						)}
					</Kategori>
				</Kategori>
			</Panel>
		</Vis>
	)
}

DokarkivForm.validation = {
	dokarkiv: ifPresent(
		'$dokarkiv',
		Yup.object({
			tittel: requiredString,
			tema: requiredString,
			journalfoerendeEnhet: Yup.string()
				.optional()
				.nullable()
				.matches(/^\d*$/, 'Journalfoerende enhet må enten være blank eller et tall med 4 sifre')
				.test(
					'len',
					'Journalfoerende enhet må enten være blank eller et tall med 4 sifre',
					(val) => !val || (val && val.length === 4),
				),
			sak: Yup.object({
				sakstype: requiredString,
				fagsaksystem: Yup.string().when('sakstype', {
					is: 'FAGSAK',
					then: () => requiredString,
					otherwise: () => Yup.mixed().notRequired(),
				}),
				fagsakId: Yup.string().when('sakstype', {
					is: 'FAGSAK',
					then: () => requiredString,
					otherwise: () => Yup.mixed().notRequired(),
				}),
			}),
			avsenderMottaker: Yup.object({
				idType: Yup.string().optional().nullable(),
				id: Yup.string()
					.when('idType', {
						is: 'ORGNR',
						then: () =>
							Yup.string()
								.matches(/^\d*$/, 'Orgnummer må være et tall med 9 sifre')
								.test(
									'len',
									'Orgnummer må være et tall med 9 sifre',
									(val) => val && val.length === 9,
								),
					})
					.when('idType', {
						is: 'FNR',
						then: () =>
							Yup.string()
								.matches(/^\d*$/, 'Ident må være et tall med 11 sifre')
								.test(
									'len',
									'Ident må være et tall med 11 sifre',
									(val) => val && val.length === 11,
								),
					}),
				navn: Yup.string().optional(),
			}),
			dokumenter: Yup.array().of(
				Yup.object({
					tittel: requiredString,
					brevkode: Yup.string().test(
						'is-valid-brevkode',
						'Feltet er påkrevd',
						function validBrevkode() {
							const values = this.options.context
							const brevkode = _.get(values, 'dokarkiv.dokumenter[0].brevkode')
							return brevkode !== ''
						},
					),
				}),
			),
		}),
	),
}
