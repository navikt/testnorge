import React, { useEffect, useState } from 'react'
import * as Yup from 'yup'
import { ifPresent, messages, requiredDate, requiredString } from '@/utils/YupValidations'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { Vedlegg } from '@/components/fagsystem/dokarkiv/form/DokarkivForm'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { useNavEnheter } from '@/utils/hooks/useNorg2'
import _ from 'lodash'
import { Option } from '@/service/SelectOptionsOppslag'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { useFormContext } from 'react-hook-form'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { getYearRangeOptions } from '@/utils/DataFormatter'
import { initialHistark } from '@/components/fagsystem/histark/form/initialValues'
import { DisplayFormError } from '@/components/ui/toast/DisplayFormError'

const DokumentInfoListe = React.lazy(
	() => import('@/components/fagsystem/dokarkiv/modal/DokumentInfoListe'),
)
const FileUploader = React.lazy(() => import('@/utils/FileUploader/FileUploader'))

enum Kodeverk {
	TEMA = 'Tema',
}

export const histarkAttributt = 'histark'

export const HistarkForm = () => {
	const formMethods = useFormContext()
	if (!_.has(formMethods.getValues(), histarkAttributt)) {
		return null
	}

	const sessionDokumenter = formMethods.watch('histark.vedlegg')
	const [files, setFiles] = useState(sessionDokumenter || [])
	const [selectedNavEnhet, setSelectedNavEnhet] = useState(
		formMethods.watch('histark.dokumenter.0.enhetsnummer'),
	)

	const { navEnheter = [] } = useNavEnheter()

	useEffect(() => {
		handleVedleggChange(files)
	}, [files])
	const handleVedleggChange = (filer: [Vedlegg]) => {
		setFiles(filer)
		formMethods.setValue('histark.vedlegg', filer)
		formMethods.setValue('histark.dokumenter.0.tittel', null)
		formMethods.setValue('histark.dokumenter.0.antallSider', null)
		formMethods.setValue('histark.dokumenter.0.fysiskDokument', null)

		filer.forEach((fil: Vedlegg, index: number) => {
			formMethods.setValue(`histark.dokumenter.${index}.tittel`, fil.dokNavn || fil.name)
			formMethods.setValue(`histark.dokumenter.${index}.antallSider`, 1)
			formMethods.setValue(`histark.dokumenter.${index}.fysiskDokument`, fil.content.base64)
		})
		formMethods.trigger('histark.dokumenter')
		formMethods.trigger('histark.vedlegg')
	}

	return (
		// @ts-ignore
		<Vis attributt={histarkAttributt}>
			<Panel
				heading="Dokumenter (Histark)"
				hasErrors={panelError(histarkAttributt)}
				iconType="dokarkiv"
				// @ts-ignore
				startOpen={erForsteEllerTest(formMethods.getValues(), [histarkAttributt])}
			>
				<Kategori title={`Oppretting av saksmappe for histark`} vis={histarkAttributt}>
					<FormDollyFieldArray
						name="histark.dokumenter"
						header="Dokumenter"
						newEntry={initialHistark}
						maxEntries={1} //Foreløpig er bare 1 innsending støttet, backend har mulighet for flere
						canBeEmpty={false}
					>
						{(path: string) => (
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
									<Kategori title={'Vedlegg'}>
										<FileUploader filer={files} setFiler={setFiles} isMultiple={false} />
										{files.length > 0 && (
											<DokumentInfoListe
												handleChange={handleVedleggChange}
												filer={files}
												isMultiple={false}
											/>
										)}
									</Kategori>
									<DisplayFormError path={`${path}.tittel`} errorMessage={'Vedlegg er påkrevd'} />
								</div>
							</div>
						)}
					</FormDollyFieldArray>
				</Kategori>
			</Panel>
		</Vis>
	)
}

HistarkForm.validation = {
	histark: ifPresent(
		'$histark',
		Yup.object({
			dokumenter: Yup.array().of(
				Yup.object({
					tittel: requiredString,
					temakoder: Yup.array().required().min(1, 'Velg minst en temakode'),
					enhetsnavn: Yup.string().required('Velg en NAV-enhet'),
					enhetsnummer: requiredString,
					skanner: requiredString,
					skannested: requiredString,
					skanningsTidspunkt: requiredDate.nullable(),
					startYear: Yup.number()
						.required(messages.required)
						.test('start-before-slutt', 'Startår må være før sluttår', (value, context) => {
							const sluttAar = context.parent.endYear
							return value && sluttAar && value < sluttAar
						}),
					endYear: Yup.number()
						.required(messages.required)
						.test('slutt-after-start', 'Sluttår må være etter startår', (value, context) => {
							const startAar = context.parent.startYear
							return value && startAar && value > startAar
						}),
				}),
			),
		}),
	),
}
