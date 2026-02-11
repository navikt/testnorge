import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { DollyTextInput, FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { BaseSyntheticEvent, useEffect, useRef, useState } from 'react'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import Digitalinnsending from '@/components/fagsystem/dokarkiv/form/partials/Digitalinnsending'
import { Alert, FileObject, FileUpload, Heading, VStack } from '@navikt/ds-react'
import Loading from '@/components/ui/loading/Loading'
import { Option } from '@/service/SelectOptionsOppslag'
import { useKodeverk } from '@/utils/hooks/useKodeverk'
import { useDokumenterFraMal } from '@/utils/hooks/useDokumenter'
import {
	BestillingsveilederContextType,
	useBestillingsveileder,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { UseFormReturn } from 'react-hook-form'

type Skjema = {
	data: string
	label: string
	lowercaseLabel: string
	value: string
}

export enum Kodeverk {
	TEMA = 'Tema',
	NAVSKJEMA = 'NAVSkjema',
	BEHANDLINGSTEMA = 'Behandlingstema',
}

type DokumentProps = {
	path: string
	formMethods: UseFormReturn
	digitalInnsending?: boolean
}

type Dokumentvariant = {
	filtype: string
	fysiskDokument: string | ArrayBuffer | undefined
	variantformat: string
	/** optional referanse id fra mal */
	dokumentReferanse?: string
}

type DokumentObjekt = {
	tittel: string
	brevkode: string
	dokumentvarianter?: Dokumentvariant[]
}

export const Dokument = ({ path, formMethods, digitalInnsending }: DokumentProps) => {
	const opts = useBestillingsveileder() as BestillingsveilederContextType
	const malId = (opts?.mal as any)?.id
	const {
		dokumenter: dokumenterFraMal,
		loading: loadingDokumenterFraMal,
		error: errorDokumenterFraMal,
	} = useDokumenterFraMal(malId)

	const hasProcessedMalRef = useRef(false)
	const [vedlegg, setVedlegg] = useState<FileObject[]>(
		() => formMethods.getValues(`${path}.vedlegg`) || [],
	)

	const { kodeverk: behandlingstemaKodeverk, loading } = useKodeverk(Kodeverk.BEHANDLINGSTEMA)

	useEffect(() => {
		if (hasProcessedMalRef.current || !(dokumenterFraMal as any)?.length) {
			return
		}
		hasProcessedMalRef.current = true

		const currentDokumenter: DokumentObjekt[] =
			formMethods.getValues(`${path}.dokumenter`) || []
		const vedleggFraMal: FileObject[] = []

		;(dokumenterFraMal as any).forEach((malDokument: any) => {
			currentDokumenter.forEach((dokument: DokumentObjekt, idx: number) => {
				dokument?.dokumentvarianter?.forEach((variant: Dokumentvariant, idy: number) => {
					if (variant?.dokumentReferanse === malDokument?.id) {
						formMethods.setValue(
							`${path}.dokumenter[${idx}].dokumentvarianter[${idy}].fysiskDokument`,
							malDokument?.contents,
						)
					}
				})
			})

			const fileName = currentDokumenter.find((dok: DokumentObjekt) =>
				dok?.dokumentvarianter?.find(
					(variant: Dokumentvariant) => variant.dokumentReferanse === malDokument.id,
				),
			)?.tittel
			if (fileName) {
				vedleggFraMal.push({
					file: new File([malDokument.contents], fileName, { type: 'application/pdf' }),
					error: false,
					reasons: [],
				})
			}
		})

		if (vedleggFraMal.length > 0) {
			const newVedlegg = [...vedleggFraMal, ...formMethods.getValues(`${path}.vedlegg`) || []]
			setVedlegg(newVedlegg)
			formMethods.setValue(`${path}.vedlegg`, newVedlegg)
		}
	}, [dokumenterFraMal])

	const updateVedlegg = (newVedlegg: FileObject[]) => {
		setVedlegg(newVedlegg)
		formMethods.setValue(`${path}.vedlegg`, newVedlegg)
		formMethods.trigger(`${path}.vedlegg`)
	}

	const updateDokumenter = (newDokumenter: DokumentObjekt[]) => {
		formMethods.setValue(`${path}.dokumenter`, newDokumenter)
		formMethods.trigger(`${path}.dokumenter`)
	}

	const handleSkjemaChange = (skjema: Skjema) => {
		if (!skjema) {
			return
		}
		formMethods.setValue(`${path}.tittel`, skjema.data)
		formMethods.setValue(`${path}.skjema`, skjema)
		const currentDokumenter: DokumentObjekt[] =
			formMethods.getValues(`${path}.dokumenter`) || []
		currentDokumenter.forEach((dokument: DokumentObjekt, idx: number) => {
			formMethods.setValue(`${path}.dokumenter[${idx}].brevkode`, skjema.value)
			if (!dokument?.tittel) {
				formMethods.setValue(`${path}.dokumenter[${idx}].tittel`, skjema.data)
			}
		})
		formMethods.trigger(`${path}.dokumenter`)
	}

	const handleSakstypeChange = (target: Option) => {
		formMethods.setValue(`${path}.sak.sakstype`, target.value)
		if (target.value !== 'FAGSAK') {
			formMethods.setValue(`${path}.sak.fagsaksystem`, '')
			formMethods.setValue(`${path}.sak.fagsakId`, '')
		}
		formMethods.trigger(`${path}.sak`)
	}

	const handleSelectFiles = (newFiles: FileObject[], files: File[]) => {
		const currentDokumenter: DokumentObjekt[] =
			formMethods.getValues(`${path}.dokumenter`) || []
		const dokumenterIsEmpty =
			currentDokumenter.length === 1 && !currentDokumenter[0]?.dokumentvarianter
		const newDokumenter: DokumentObjekt[] = dokumenterIsEmpty ? [] : [...currentDokumenter]
		const brevkode = formMethods.getValues(`${path}.skjema`)?.value || ''

		updateVedlegg([...vedlegg, ...newFiles])

		files.forEach((file: File) => {
			const reader = new FileReader()
			reader.onabort = () => console.warn('file reading was aborted')
			reader.onerror = () => console.error('file reading has failed')
			reader.onload = () => {
				const binaryStr = reader.result?.slice(28)
				newDokumenter.push({
					tittel: file.name,
					brevkode,
					dokumentvarianter: [
						{
							filtype: 'PDFA',
							fysiskDokument: binaryStr,
							variantformat: 'ARKIV',
						},
					],
				})
				updateDokumenter(newDokumenter)
			}
			reader.readAsDataURL(file)
		})
	}

	const handleDeleteFile = (file: FileObject) => {
		updateVedlegg(vedlegg.filter((f) => f !== file))
		const currentDokumenter: DokumentObjekt[] =
			formMethods.getValues(`${path}.dokumenter`) || []
		const index = currentDokumenter.findIndex(
			(d: DokumentObjekt) => d.tittel === file.file.name,
		)
		if (index >= 0) {
			const newDokumenter = [...currentDokumenter]
			newDokumenter.splice(index, 1)
			updateDokumenter(newDokumenter)
		}
	}

	const harFagsak = formMethods.watch(`${path}.sak.sakstype`) === 'FAGSAK'

	return (
		<>
			<div className="flexbox--full-width">
				<FormSelect
					name={`${path}.skjemaValg`}
					label="Skjema"
					afterChange={handleSkjemaChange}
					kodeverk={Kodeverk.NAVSKJEMA}
					size="grow"
					optionHeight={50}
					isClearable={true}
				/>
			</div>
			<div className="flexbox--flex-wrap">
				<FormTextInput
					name={`${path}.dokumenter[0].brevkode`}
					label="Brevkode"
					size="large"
				/>
				<FormSelect
					name={`${path}.tema`}
					label="Tema"
					kodeverk={Kodeverk.TEMA}
					size="xlarge"
					isClearable={false}
				/>
				<FormSelect
					name={`${path}.behandlingstema`}
					label="Behandlingstema"
					size={'xlarge'}
					options={!loading && behandlingstemaKodeverk}
					isClearable={true}
				/>
				<DollyTextInput
					onChange={(event: BaseSyntheticEvent) => {
						formMethods.setValue(
							`${path}.journalfoerendeEnhet`,
							event.target.value === '' ? undefined : event.target.value,
						)
						formMethods.trigger(`${path}.journalfoerendeEnhet`)
					}}
					name={`${path}.journalfoerendeEnhet`}
					label="Journalførende enhet"
					size="large"
				/>
				<FormSelect
					name={`${path}.sak.sakstype`}
					label="Sakstype"
					options={Options('sakstype')}
					onChange={handleSakstypeChange}
					isClearable={false}
				/>
				{harFagsak && (
					<>
						<FormSelect
							name={`${path}.sak.fagsaksystem`}
							label="Fagsaksystem"
							options={Options('fagsaksystem')}
							size="large"
						/>
						<FormTextInput name={`${path}.sak.fagsakId`} label="Fagsak-ID" />
					</>
				)}
			</div>
			<FormCheckbox
				id={`${path}.ferdigstill`}
				name={`${path}.ferdigstill`}
				label="Ferdigstill journalpost"
			/>
			{digitalInnsending ? <Digitalinnsending path={path} /> : null}
			<VStack gap="4" style={{ margin: '10px 0 15px 0', width: '100%' }}>
				<FileUpload.Dropzone
					label="Last opp vedlegg til dokumentet"
					description={`Du kan laste opp PDF-filer. Maks 10 filer.`}
					accept=".pdf"
					fileLimit={{ max: 10, current: vedlegg?.length }}
					onSelect={(selectedFiles) =>
						handleSelectFiles(
							selectedFiles,
							selectedFiles.map((f) => f.file),
						)
					}
				/>
				{vedlegg?.length < 1 && loadingDokumenterFraMal && malId && (
					<Loading label="Laster vedlegg fra mal ..." />
				)}
				{errorDokumenterFraMal && malId && (
					<Alert variant="error" size="small">
						{errorDokumenterFraMal.message}
					</Alert>
				)}
				{vedlegg?.length > 0 && (
					<VStack gap="2">
						<Heading level="3" size="xsmall">{`Vedlegg (${vedlegg?.length})`}</Heading>
						<VStack as="ul" gap="3">
							{vedlegg?.map((file, idx) => (
								<FileUpload.Item
									as="li"
									key={file?.file?.name + idx}
									file={file?.file}
									button={{ action: 'delete', onClick: () => handleDeleteFile(file) }}
								/>
							))}
						</VStack>
					</VStack>
				)}
			</VStack>
		</>
	)
}
