import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { Option } from '@/service/SelectOptionsOppslag'
import * as _ from 'lodash-es'
import { getYearRangeOptions } from '@/utils/DataFormatter'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FileObject, FileUpload, VStack } from '@navikt/ds-react'
import { DisplayFormError } from '@/components/ui/toast/DisplayFormError'
import React, { useEffect, useState } from 'react'
import { useNavEnheter } from '@/utils/hooks/useNorg2'

type Vedlegg = {
	file: File
	error: boolean
	reasons: string[]
}

enum Kodeverk {
	TEMA = 'TemaHistark',
}

export const HistarkDokument = ({ path, formMethods }) => {
	const [vedlegg, setVedlegg] = useState<FileObject[]>(formMethods.watch(`${path}.vedlegg`) || [])
	const [selectedNavEnhet, setSelectedNavEnhet] = useState(
		formMethods.watch(`${path}.enhetsnummer`),
	)

	const { navEnheter = [] } = useNavEnheter()

	useEffect(() => {
		handleVedleggChange(vedlegg)
	}, [vedlegg])

	const handleVedleggChange = (filer: Vedlegg[]) => {
		setVedlegg(filer)
		formMethods.setValue(`${path}.vedlegg`, filer)

		if (!filer || filer.length < 1) {
			formMethods.setValue(`${path}.tittel`, null)
			formMethods.setValue(`${path}.antallSider`, null)
			formMethods.setValue(`${path}.fysiskDokument`, null)
		}

		filer?.forEach((fil: Vedlegg) => {
			const reader = new FileReader()
			reader.onabort = () => console.warn('file reading was aborted')
			reader.onerror = () => console.error('file reading has failed')
			reader.onload = () => {
				const binaryStr = reader.result?.slice(28)
				formMethods.setValue(`${path}.tittel`, fil.file?.name)
				formMethods.setValue(`${path}.antallSider`, 1)
				formMethods.setValue(`${path}.fysiskDokument`, binaryStr)
			}
			reader.readAsDataURL(fil.file)
		})
		formMethods.trigger(path)
	}

	return (
		<div className="flexbox--column">
			<div className="flexbox--flex-wrap">
				<div className="flexbox--full-width">
					<FormSelect
						name={`${path}.temakoder`}
						label="Temakoder"
						kodeverk={Kodeverk.TEMA}
						size="full-width"
						isClearable={false}
						isMulti={true}
					/>
				</div>
				<FormSelect
					name={'navenhet'}
					fieldName={`${path}.enhetsnavn`}
					value={selectedNavEnhet}
					onChange={(selected: Option) => {
						formMethods.setValue(`${path}.enhetsnummer`, selected?.value || null)
						formMethods.setValue(`${path}.enhetsnavn`, selected?.label || null)
						setSelectedNavEnhet(selected?.value)
						formMethods.trigger(path)
					}}
					label={'NAV-enhet'}
					size={'xlarge'}
					options={navEnheter}
					isLoading={_.isEmpty(navEnheter)}
				/>
				<FormSelect
					name={`${path}.startYear`}
					label="Startår"
					options={getYearRangeOptions(1980, 2019)}
					afterChange={() => formMethods.trigger(path)}
				/>
				<FormSelect
					name={`${path}.endYear`}
					label="Sluttår"
					options={getYearRangeOptions(1980, 2019)}
					afterChange={() => formMethods.trigger(path)}
				/>
				<FormDatepicker
					format={'DD.MM.YYYY HH:mm'}
					name={`${path}.skanningsTidspunkt`}
					label="Skanningstidspunkt"
					visHvisAvhuket={false}
				/>
				<FormTextInput
					name={`${path}.skanner`}
					label="Skanner"
					visHvisAvhuket={false}
					size={'xsmall'}
				/>
				<FormTextInput
					name={`${path}.skannested`}
					label="Skannested"
					visHvisAvhuket={false}
					size={'xsmall'}
				/>
				<div className="flexbox--full-width">
					<VStack gap="4" style={{ marginTop: '10px' }}>
						<FileUpload.Dropzone
							label="Last opp vedlegg til dokumentet"
							accept=".pdf"
							fileLimit={{ max: 1, current: vedlegg.length }}
							multiple={false}
							onSelect={setVedlegg}
						/>
						{vedlegg.map((file: FileObject) => (
							<FileUpload.Item
								as="li"
								key={file.file?.name}
								file={file.file}
								button={{
									action: 'delete',
									onClick: () => setVedlegg([]),
								}}
							/>
						))}
					</VStack>
				</div>
				<DisplayFormError path={`${path}.tittel`} errorMessage={'Vedlegg er påkrevd'} />
			</div>
		</div>
	)
}
