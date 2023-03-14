import React, { BaseSyntheticEvent, useCallback, useEffect, useState } from 'react'
import * as Yup from 'yup'
import { ifPresent, requiredString } from '@/utils/YupValidations'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { DollyTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { FormikProps } from 'formik'
import styled from 'styled-components'
import * as _ from 'lodash-es'
import { Digitalinnsending } from '@/components/fagsystem/dokarkiv/form/partials/Digitalinnsending'
import { DokumentInfoListe } from '@/components/fagsystem/dokarkiv/modal/DokumentInfoListe'
import { useDropzone } from 'react-dropzone'

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

const getColor = (props: any) => {
	if (props.isDragAccept) {
		return '#06893a'
	}
	if (props.isDragReject) {
		return '#ba3a26'
	}
	if (props.isFocused) {
		return '#0067C5FF'
	}
	return '#eeeeee'
}

const Container = styled.div`
	flex: 1;
	display: flex;
	flex-direction: column;
	align-items: center;
	margin-bottom: 10px;
	padding: 20px;
	border-width: 2px;
	border-radius: 2px;
	border-color: ${(props) => getColor(props)};
	border-style: dashed;
	background-color: #fafafa;
	color: #bdbdbd;
	outline: none;
	transition: border 0.24s ease-in-out;
`

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

	const MyDropzone = () => {
		const handleDrop = useCallback((acceptedFiles: File[]) => {
			const reader = new FileReader()

			acceptedFiles.forEach((file: File) => {
				reader.onabort = () => console.warn('file reading was aborted')
				reader.onerror = () => console.error('file reading has failed')
				reader.onload = () => {
					const binaryStr = reader.result?.slice(28)
					setFiles([
						// @ts-ignore
						{
							id: new Date().getTime(),
							name: file.path,
							content: { base64: binaryStr },
						},
						...files,
					])
				}
				reader.readAsDataURL(file)
			})
		}, [])
		const { getRootProps, getInputProps, isFocused, isDragAccept, isDragReject } = useDropzone({
			multiple: true,
			onDrop: handleDrop,
		})

		return (
			<div className="container">
				<Container {...getRootProps({ isFocused, isDragAccept, isDragReject })}>
					<input {...getInputProps()} />
					<p>Dra og slipp filer innenfor rammen eller klikk her for å åpne filvelger</p>
				</Container>
			</div>
		)
	}

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

	return (
		// @ts-ignore
		<Vis attributt={dokarkivAttributt}>
			<Panel
				heading="Dokumenter"
				hasErrors={panelError(formikBag, dokarkivAttributt)}
				iconType="dokarkiv"
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
								event.target.value === '' ? undefined : event.target.value
							)
						}}
						feil={
							_.get(formikBag.errors, `dokarkiv.journalfoerendeEnhet`)
								? { feilmelding: _.get(formikBag.errors, `dokarkiv.journalfoerendeEnhet`) }
								: null
						}
						name="dokarkiv.journalfoerendeEnhet"
						label="Journalførende enhet"
					/>
					{digitalInnsending ? <Digitalinnsending /> : null}
					<Kategori title={'Vedlegg'}>
						<MyDropzone />
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
					(val) => !val || (val && val.length === 4)
				),
			avsenderMottaker: Yup.object({
				idType: Yup.string().optional().nullable(),
				id: Yup.string()
					.when('idType', {
						is: 'ORGNR',
						then: Yup.string()
							.matches(/^\d*$/, 'Orgnummer må være et tall med 9 sifre')
							.test(
								'len',
								'Orgnummer må være et tall med 9 sifre',
								(val) => val && val.length === 9
							),
					})
					.when('idType', {
						is: 'FNR',
						then: Yup.string()
							.matches(/^\d*$/, 'Ident må være et tall med 11 sifre')
							.test('len', 'Ident må være et tall med 11 sifre', (val) => val && val.length === 11),
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
						}
					),
				})
			),
		})
	),
}
