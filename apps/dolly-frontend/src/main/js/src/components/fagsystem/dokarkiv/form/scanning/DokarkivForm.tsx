import React, { useEffect, useState } from 'react'
import * as Yup from 'yup'
import { ifPresent, requiredString } from '~/utils/YupValidations'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import Panel from '~/components/ui/panel/Panel'
import { erForste, panelError } from '~/components/ui/form/formUtils'
import { FormikProps } from 'formik'
import FileUpload from 'filopplasting'
import { pdfjs } from 'react-pdf'
// @ts-ignore
import pdfjsWorker from 'pdfjs-dist/build/pdf.worker.entry'
import styled from 'styled-components'
import _get from 'lodash/get'
import { Digitalinnsending } from '~/components/fagsystem/dokarkiv/form/digital/Digitalinnsending'
import useBoolean from '~/utils/hooks/useBoolean'
import { FilnavnModal } from '~/components/fagsystem/dokarkiv/modal/FilnavnModal'

pdfjs.GlobalWorkerOptions.workerSrc = pdfjsWorker

interface DokarkivForm {
	formikBag: FormikProps<{}>
}

type Skjema = {
	data: string
	label: string
	lowercaseLabel: string
	value: string
}

type Vedlegg = {
	id: string
	name: string
	content: {
		base64: string
	}
}

const FilOpplaster = styled(FileUpload)`
	background-color: unset;
	margin-bottom: 10px;

	&:hover {
		background-color: #f1f1f1;
	}
`

enum Kodeverk {
	TEMA = 'Tema',
	NAVSKJEMA = 'NAVSkjema'
}

const dokarkivAttributt = 'dokarkiv'

export const DokarkivForm = ({ formikBag }: DokarkivForm) => {
	const gjeldendeFiler = JSON.parse(sessionStorage.getItem('dokarkiv_vedlegg'))
	const digitalInnsending = _get(formikBag.values, 'dokarkiv.avsenderMottaker')
	const [files, setFiles] = useState(gjeldendeFiler ? gjeldendeFiler : [])

	const [isFilnavnModalOpen, openFilnavnModal, closeFilnavnModal] = useBoolean(false)
	const [skjemaValues, setSkjemaValues] = useState(null)

	useEffect(() => handleSkjemaChange(skjemaValues), [files, skjemaValues])

	const handleSkjemaChange = (skjema: Skjema) => {
		if (!skjema) {
			return
		}
		setSkjemaValues(skjema)
		formikBag.setFieldValue('dokarkiv.tittel', skjema.data)
		const dokumentVarianter = files.map((vedl: Vedlegg, index: number) => ({
			tittel: vedl.name,
			brevkode: (index === 0 && skjema?.value) || undefined,
			dokumentvarianter: [
				{
					filtype: 'PDFA',
					fysiskDokument: 'testy', // TODO REVERT!! vedl.content.base64,
					variantformat: 'ARKIV'
				}
			]
		}))
		dokumentVarianter.length > 0
			? formikBag.setFieldValue('dokarkiv.dokumenter', dokumentVarianter)
			: formikBag.setFieldValue('dokarkiv.dokumenter[0].tittel', skjema.data)
	}

	const handleVedleggChange = (filer: [Vedlegg]) => {
		setFiles(filer)
		openFilnavnModal()
		sessionStorage.setItem('dokarkiv_vedlegg', JSON.stringify(filer))
	}

	return (
		// @ts-ignore
		<Vis attributt={dokarkivAttributt}>
			<Panel
				heading="Dokumenter"
				hasErrors={panelError(formikBag, dokarkivAttributt)}
				iconType="dokarkiv"
				// @ts-ignore
				startOpen={() => erForste(formikBag.values, [dokarkivAttributt])}
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
					<FormikSelect
						name="dokarkiv.tema"
						label="Tema"
						kodeverk={Kodeverk.TEMA}
						size="xlarge"
						isClearable={false}
					/>
					<FormikTextInput name="dokarkiv.journalfoerendeEnhet" label="Journalførende enhet" />
					{digitalInnsending ? <Digitalinnsending /> : null}
					<Kategori title={'Vedlegg'}>
						<FilOpplaster
							className={'flexbox--full-width'}
							acceptedMimetypes={['application/pdf']}
							files={files}
							// @ts-ignore
							onFilesChanged={handleVedleggChange}
						/>
					</Kategori>
				</Kategori>
			</Panel>
			{isFilnavnModalOpen && files && <FilnavnModal closeModal={closeFilnavnModal} filer={files} />}
		</Vis>
	)
}

DokarkivForm.validation = {
	dokarkiv: ifPresent(
		'$dokarkiv',
		Yup.object({
			tittel: requiredString,
			tema: requiredString,
			journalfoerendeEnhet: Yup.string(),
			avsenderMottaker: Yup.object({
				idType: Yup.string()
					.optional()
					.nullable(),
				id: Yup.string()
					.when('idType', {
						is: 'ORGNR',
						then: Yup.string()
							.matches(/^[0-9]*$/, 'Orgnummer må være et tall med 9 sifre')
							.test('len', 'Orgnummer må være et tall med 9 sifre', val => val && val.length === 9)
					})
					.when('idType', {
						is: 'FNR',
						then: Yup.string()
							.matches(/^[0-9]*$/, 'Ident må være et tall med 11 sifre')
							.test('len', 'Ident må være et tall med 11 sifre', val => val && val.length === 11)
					}),
				navn: Yup.string().optional()
			}),
			dokumenter: Yup.array().of(
				Yup.object({
					tittel: requiredString,
					brevkode: Yup.string().test(
						'is-valid-brevkode',
						'Feltet er påkrevd',
						function validBrevkode() {
							const values = this.options.context
							const brevkode = _get(values, 'dokarkiv.dokumenter[0].brevkode')
							return brevkode !== ''
						}
					)
				})
			)
		})
	)
}
