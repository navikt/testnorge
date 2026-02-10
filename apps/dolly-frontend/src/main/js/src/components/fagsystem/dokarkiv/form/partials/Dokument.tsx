import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { DollyTextInput, FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import React, { BaseSyntheticEvent, useEffect, useRef, useState } from 'react'
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
import styled from 'styled-components'

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

export const StyledVedlegg = styled(FileUpload.Item)`
	&&& {
		a {
			color: var(--ax-text-subtle);
		}
	}
`

export const Dokument = ({ path, formMethods, digitalInnsending }: DokumentProps) => {
	const opts = useBestillingsveileder() as BestillingsveilederContextType
	const malId = (opts?.mal as any)?.id
	const {
		dokumenter: dokumenterFraMal,
		loading: loadingDokumenterFraMal,
		error: errorDokumenterFraMal,
	} = useDokumenterFraMal(malId)

	const prevDokumenterFraMalRef = useRef(dokumenterFraMal)
	const prevDokumenterFraMal = prevDokumenterFraMalRef.current

	useEffect(() => {
		prevDokumenterFraMalRef.current = dokumenterFraMal
	}, [dokumenterFraMal])

	const [vedlegg, setVedlegg] = useState<FileObject[]>(formMethods.watch(`${path}.vedlegg`) || [])
	const [dokumenter, setDokumenter] = useState<DokumentObjekt[]>(
		formMethods.watch(`${path}.dokumenter`) || [],
	)
	const [skjemaValues, setSkjemaValues] = useState<Skjema | undefined>(
		formMethods.watch(`${path}.skjema`),
	)

	const { kodeverk: behandlingstemaKodeverk, loading } = useKodeverk(Kodeverk.BEHANDLINGSTEMA)

	useEffect(() => {
		if (dokumenterFraMal !== prevDokumenterFraMal && (dokumenterFraMal as any)?.length > 0) {
			const vedleggFraMal: FileObject[] = []
			;(dokumenterFraMal as any)?.forEach((malDokument: any) => {
				dokumenter?.forEach((dokument: DokumentObjekt, idx: number) => {
					dokument?.dokumentvarianter?.forEach((variant: Dokumentvariant, idy: number) => {
						if (variant?.dokumentReferanse === malDokument?.id) {
							formMethods.setValue(
								`${path}.dokumenter[${idx}].dokumentvarianter[${idy}].fysiskDokument`,
								malDokument?.contents,
							)
						}
					})
				})
				setDokumenter(formMethods.watch(`${path}.dokumenter`) || [])

				const fileName = dokumenter?.find((dok: DokumentObjekt) =>
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
			setVedlegg([...vedleggFraMal, ...vedlegg])
		}
	}, [dokumenterFraMal, prevDokumenterFraMal])

	useEffect(() => {
		if (formMethods.watch(path)) {
			formMethods.setValue(`${path}.dokumenter`, dokumenter)
			formMethods.trigger(`${path}.dokumenter`)
		}
	}, [dokumenter])

	useEffect(() => {
		if (formMethods.watch(path)) {
			formMethods.setValue(`${path}.vedlegg`, vedlegg)
			formMethods.trigger(`${path}.vedlegg`)
		}
	}, [vedlegg])

	const handleSkjemaChange = (skjema: Skjema) => {
		if (!skjema) {
			return
		}
		setSkjemaValues(skjema)
		formMethods.setValue(`${path}.tittel`, skjema.data)
		formMethods.setValue(`${path}.skjema`, skjema)
		formMethods.watch(`${path}.dokumenter`)?.forEach((dokument: DokumentObjekt, idx: number) => {
			formMethods.setValue(`${path}.dokumenter[${idx}].brevkode`, skjema.value)
			if (!dokument?.tittel) {
				formMethods.setValue(`${path}.dokumenter[${idx}].tittel`, skjema.data)
			}
		})
	}

	const handleSakstypeChange = (target: Option) => {
		formMethods.setValue(`${path}.sak.sakstype`, target.value)
		if (target.value !== 'FAGSAK') {
			formMethods.setValue(`${path}.sak.fagsaksystem`, '')
			formMethods.setValue(`${path}.sak.fagsakId`, '')
		}
		formMethods.trigger(`${path}.sak`)
	}

	const handleSelectFiles = (selectedFiles: File[]) => {
		const dokumenterIsEmpty = dokumenter?.length === 1 && !dokumenter[0]?.dokumentvarianter
		const newDokumenter: DokumentObjekt[] = dokumenterIsEmpty ? [] : [...dokumenter]

		selectedFiles.forEach((file: File) => {
			const reader = new FileReader()
			reader.onabort = () => console.warn('file reading was aborted')
			reader.onerror = () => console.error('file reading has failed')
			reader.onload = () => {
				const binaryStr = reader.result?.slice(28)
				const dokumentvariant: Dokumentvariant = {
					filtype: 'PDFA',
					fysiskDokument: binaryStr,
					variantformat: 'ARKIV',
				}
				const newDokument: DokumentObjekt = {
					tittel: file.name,
					brevkode: skjemaValues?.value || '',
					dokumentvarianter: [dokumentvariant],
				}
				newDokumenter.push(newDokument)
				setDokumenter(newDokumenter)
			}
			reader.readAsDataURL(file)
		})
	}

	const handleDeleteFile = (file: FileObject) => {
		setVedlegg(vedlegg.filter((f) => f !== file))
		const filtrerteDokumenter: DokumentObjekt[] = [...dokumenter]
		const index = filtrerteDokumenter.findIndex((d: DokumentObjekt) => d.tittel === file.file.name)
		if (index >= 0) {
			filtrerteDokumenter.splice(index, 1)
			setDokumenter(filtrerteDokumenter)
			formMethods.setValue(`${path}.dokumenter`, filtrerteDokumenter)
			formMethods.trigger(`${path}.dokumenter`)
		}
	}

	const harFagsak = formMethods.watch(`${path}.sak.sakstype`) === 'FAGSAK'

	return (
		<>
			<div className="flexbox--full-width">
				<FormSelect
					name={`${path}.dokumenter[0].brevkode`}
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
			<VStack gap="space-16" style={{ margin: '10px 0 15px 0', width: '100%' }}>
				<FileUpload.Dropzone
					label="Last opp vedlegg til dokumentet"
					description={`Du kan laste opp PDF-filer. Maks 10 filer.`}
					accept=".pdf"
					fileLimit={{ max: 10, current: vedlegg?.length }}
					onSelect={(selectedFiles) => {
						setVedlegg([...vedlegg, ...selectedFiles])
						handleSelectFiles(selectedFiles?.map((f) => f.file))
					}}
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
					<VStack gap="space-8">
						<Heading level="3" size="xsmall">{`Vedlegg (${vedlegg?.length})`}</Heading>
						<VStack gap="space-12">
							{vedlegg?.map((file, idx) => (
								<StyledVedlegg
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
