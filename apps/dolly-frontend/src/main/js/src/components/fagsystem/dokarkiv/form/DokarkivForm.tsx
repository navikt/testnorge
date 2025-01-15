import React, { BaseSyntheticEvent, useContext, useEffect, useState } from 'react'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { DollyTextInput, FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import * as _ from 'lodash-es'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { useFormContext } from 'react-hook-form'
import { Option } from '@/service/SelectOptionsOppslag'
import { useKodeverk } from '@/utils/hooks/useKodeverk'
import Digitalinnsending from './partials/Digitalinnsending'
import FileUploader from '@/utils/FileUploader/FileUploader'
import DokumentInfoListe from '@/components/fagsystem/dokarkiv/modal/DokumentInfoListe'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import { useDokumenterFraMal } from '@/utils/hooks/useDokumenter'
import Loading from '@/components/ui/loading/Loading'
import {
	FileObject,
	FileRejected,
	FileRejectionReason,
	FileUpload,
	Heading,
	VStack,
} from '@navikt/ds-react'

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

const dokarkivAttributt = 'dokarkiv'

const DokarkivForm = () => {
	const opts = useContext(BestillingsveilederContext)
	const malId = opts?.mal?.id
	const {
		dokumenter: dokumenterFraMal,
		loading: loadingDokumenterFraMal,
		error,
	} = useDokumenterFraMal(malId)

	// console.log('dokumenterFraMal: ', dokumenterFraMal) //TODO - SLETT MEG

	const formMethods = useFormContext()
	if (!formMethods.watch(dokarkivAttributt)) {
		return null
	}

	const digitalInnsending = formMethods.watch('dokarkiv.avsenderMottaker')

	const [vedlegg, setVedlegg] = useState<FileObject[]>(formMethods.watch('dokarkiv.vedlegg'))
	const [dokumenter, setDokumenter] = useState(formMethods.watch('dokarkiv.dokumenter'))
	const [skjemaValues, setSkjemaValues] = useState(formMethods.watch('dokarkiv.skjema'))

	const { kodeverk: behandlingstemaKodeverk, loading } = useKodeverk('Behandlingstema')
	const { kodeverk: navSkjemaKodeverk } = useKodeverk('NAVSkjema')

	if (!_.has(formMethods.getValues(), dokarkivAttributt)) {
		return null
	}

	// useEffect(() => {
	// 	const vedlegg = formMethods.watch('dokarkiv.vedlegg')
	//
	// 	const vedleggFraMal = dokumenterFraMal?.map((dokument: any, index: number) => ({
	// 		id: new Date().getTime(),
	// 		name: `Testings ${index + 1}`,
	// 		content: {
	// 			base64: dokument?.contents,
	// 		},
	// 		...vedlegg,
	// 	}))
	// 	setVedlegg(vedleggFraMal)
	// 	formMethods.trigger('dokarkiv')
	// }, [dokumenterFraMal])

	useEffect(() => {
		formMethods.setValue('dokarkiv.dokumenter', dokumenter)
		formMethods.trigger('dokarkiv.dokumenter')
	}, [dokumenter])

	useEffect(() => {
		formMethods.setValue('dokarkiv.vedlegg', vedlegg)
		formMethods.trigger('dokarkiv.vedlegg')
	}, [vedlegg])

	const handleSkjemaChange = (skjema: Skjema) => {
		if (!skjema) {
			return
		}
		setSkjemaValues(skjema)
		formMethods.setValue('dokarkiv.tittel', skjema.data)
		formMethods.setValue('dokarkiv.skjema', skjema)
		formMethods.setValue('dokarkiv.dokumenter[0].brevkode', skjema.value) // TODO sett paa alle?
		// formMethods.trigger('dokarkiv')
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

	console.log('vedlegg: ', vedlegg) //TODO - SLETT MEG
	console.log('dokumenter: ', dokumenter) //TODO - SLETT MEG
	console.log('skjemaValues: ', skjemaValues) //TODO - SLETT MEG

	const handleSelectFiles = (selectedFiles: File[]) => {
		const reader = new FileReader()
		selectedFiles.forEach((file: File, idx: number) => {
			// const brevkode = idx === 0 && skjemaValues?.value ? skjemaValues?.value : undefined
			const erForsteDokument =
				idx === 0 && dokumenter?.length === 1 && !dokumenter[0]?.dokumentvarianter

			reader.onabort = () => console.warn('file reading was aborted')
			reader.onerror = () => console.error('file reading has failed')
			reader.onload = () => {
				const binaryStr = reader.result?.slice(28)
				const dokumentvariant = {
					filtype: 'PDFA',
					fysiskDokument: binaryStr,
					variantformat: 'ARKIV',
				}
				erForsteDokument
					? setDokumenter([
							{
								tittel: file.name,
								brevkode: skjemaValues?.value || '',
								dokumentvarianter: [dokumentvariant],
							},
						])
					: setDokumenter([
							...dokumenter,
							{
								tittel: file.name,
								// brevkode: skjemaValues?.value, //TODO: Sjekk om det gaar aa sette brevkode paa alle
								dokumentvarianter: [dokumentvariant],
							},
						])
			}
			reader.readAsDataURL(file)
		})
	}

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
					<VStack gap="4" style={{ margin: '10px 0 15px 0' }}>
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
						{vedlegg?.length > 0 && (
							<VStack gap="2">
								<Heading level="3" size="xsmall">
									{`Vedlegg (${vedlegg?.length})`}
								</Heading>
								<VStack as="ul" gap="3">
									{vedlegg?.map((file, index) => (
										<FileUpload.Item
											as="li"
											key={index}
											file={file?.file}
											button={{
												action: 'delete',
												onClick: () => console.log('Delete!'), //TODO - SLETT MEG,
												// onClick: () => setVedlegg(vedlegg.filter((f) => f !== file)),
												// onClick: () => removeFile(file),
											}}
										/>
									))}
								</VStack>
							</VStack>
						)}
					</VStack>
				</Kategori>
			</Panel>
		</Vis>
	)
}

export default DokarkivForm
