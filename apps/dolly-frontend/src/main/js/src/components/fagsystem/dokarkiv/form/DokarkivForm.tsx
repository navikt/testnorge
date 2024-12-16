import React, { BaseSyntheticEvent, useEffect, useState } from 'react'
import * as Yup from 'yup'
import { ifPresent, requiredString } from '@/utils/YupValidations'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { DollyTextInput, FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import * as _ from 'lodash-es'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { UseFormReturn } from 'react-hook-form/dist/types'
import { useFormContext } from 'react-hook-form'
import { Option } from '@/service/SelectOptionsOppslag'
import { useKodeverk } from '@/utils/hooks/useKodeverk'

const Digitalinnsending = React.lazy(
	() => import('@/components/fagsystem/dokarkiv/form/partials/Digitalinnsending'),
)

const FileUploader = React.lazy(() => import('@/utils/FileUploader/FileUploader'))

const DokumentInfoListe = React.lazy(
	() => import('@/components/fagsystem/dokarkiv/modal/DokumentInfoListe'),
)

interface DokarkivFormProps {
	formMethods: UseFormReturn
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
	BEHANDLINGSTEMA = 'Behandlingstema',
}

export const dokarkivAttributt = 'dokarkiv'

export const DokarkivForm = () => {
	const formMethods = useFormContext()
	if (!formMethods.watch(dokarkivAttributt)) {
		return null
	}
	const sessionDokumenter = formMethods.watch('dokarkiv.vedlegg')
	const digitalInnsending = formMethods.watch('dokarkiv.avsenderMottaker')
	const [files, setFiles] = useState(sessionDokumenter || [])
	const [skjemaValues, setSkjemaValues] = useState(formMethods.watch('dokarkiv.skjema'))
	const { kodeverk: behandlingstemaKodeverk, loading } = useKodeverk('Behandlingstema')

	if (!_.has(formMethods.getValues(), dokarkivAttributt)) {
		return null
	}

	useEffect(() => {
		handleSkjemaChange(skjemaValues)
		handleVedleggChange(files)
	}, [files, skjemaValues])

	const handleSkjemaChange = (skjema: Skjema) => {
		if (!skjema) {
			return
		}

		setSkjemaValues(skjema)
		formMethods.setValue('dokarkiv.tittel', skjema.data)
		formMethods.setValue('dokarkiv.skjema', skjema)

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
			? formMethods.setValue('dokarkiv.dokumenter', dokumentVarianter)
			: formMethods.setValue('dokarkiv.dokumenter[0].tittel', skjema.data)
		formMethods.trigger('dokarkiv.dokumenter')
	}

	const handleVedleggChange = (filer: [Vedlegg]) => {
		setFiles(filer)
		formMethods.setValue('dokarkiv.vedlegg', filer)
		formMethods.trigger('dokarkiv.vedlegg')
	}

	const handleSakstypeChange = (target: Option) => {
		formMethods.setValue('dokarkiv.sak.sakstype', target.value)
		if (target.value !== 'FAGSAK') {
			formMethods.setValue('dokarkiv.sak.fagsaksystem', '')
			formMethods.setValue('dokarkiv.sak.fagsakId', '')
		}
		formMethods.trigger('dokarkiv.sak')
	}

	const harFagsak = formMethods.watch('dokarkiv.sak.sakstype') === 'FAGSAK'

	return (
		// @ts-ignore
		<Vis attributt={dokarkivAttributt}>
			<Panel
				heading="Dokumenter (Joark)"
				hasErrors={panelError(dokarkivAttributt)}
				iconType="dokarkiv"
				// @ts-ignore
				startOpen={erForsteEllerTest(formMethods.getValues(), [dokarkivAttributt])}
			>
				<Kategori
					title={`Oppretting av ${digitalInnsending ? 'digitalt' : 'skannet '} dokument`}
					vis={dokarkivAttributt}
				>
					<div className="flexbox--full-width">
						<FormSelect
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
						<FormSelect
							name="dokarkiv.tema"
							label="Tema"
							kodeverk={Kodeverk.TEMA}
							size="xlarge"
							isClearable={false}
						/>
						<FormSelect
							name="dokarkiv.behandlingstema"
							label="Behandlingstema"
							size={'xxlarge'}
							options={!loading && behandlingstemaKodeverk}
							isClearable={true}
						/>
						<DollyTextInput
							onChange={(event: BaseSyntheticEvent) => {
								formMethods.setValue(
									'dokarkiv.journalfoerendeEnhet',
									event.target.value === '' ? undefined : event.target.value,
								)
								formMethods.trigger('dokarkiv.journalfoerendeEnhet')
							}}
							name="dokarkiv.journalfoerendeEnhet"
							label="Journalførende enhet"
							size="large"
						/>
						<FormSelect
							name="dokarkiv.sak.sakstype"
							label="Sakstype"
							options={Options('sakstype')}
							onChange={handleSakstypeChange}
							isClearable={false}
						/>
						{harFagsak && (
							<>
								<FormSelect
									name="dokarkiv.sak.fagsaksystem"
									label="Fagsaksystem"
									options={Options('fagsaksystem')}
									size="large"
								/>
								<FormTextInput name="dokarkiv.sak.fagsakId" label="Fagsak-ID" />
							</>
						)}
					</div>
					<FormCheckbox name={`dokarkiv.ferdigstill`} label="Ferdigstill journalpost" />
					{digitalInnsending ? <Digitalinnsending /> : null}
					<Kategori title={'Vedlegg'}>
						<FileUploader filer={files} setFiler={setFiles} />
						{files.length > 0 && (
							<DokumentInfoListe
								handleChange={handleVedleggChange}
								path={'dokarkiv.dokumenter'}
								filer={files}
							/>
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
						(_val, testContext) => {
							const fullForm =
								testContext.from && testContext.from[testContext.from.length - 1]?.value
							const brevkode = _.get(fullForm, 'dokarkiv.dokumenter[0].brevkode')
							return brevkode !== ''
						},
					),
				}),
			),
		}),
	),
}
