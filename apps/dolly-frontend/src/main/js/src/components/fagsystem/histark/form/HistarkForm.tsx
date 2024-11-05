import React, { useEffect, useState } from 'react'
import * as Yup from 'yup'
import { ifPresent, requiredDate, requiredString } from '@/utils/YupValidations'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { Vedlegg } from '@/components/fagsystem/dokarkiv/form/DokarkivForm'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { initialHistark } from '@/components/fagsystem/arbeidsplassen/form/initialValues'
import { useNavEnheter } from '@/utils/hooks/useNorg2'
import _ from 'lodash'
import { Option } from '@/service/SelectOptionsOppslag'
import { FormDateTimepicker } from '@/components/ui/form/inputs/timepicker/Timepicker'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { Yearpicker } from '@/components/ui/form/inputs/yearpicker/Yearpicker'
import { testDatoFom, testDatoTom } from '@/components/fagsystem/utils'
import { useFormContext } from 'react-hook-form'

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
	const [startAar, setStartAar] = useState(new Date())
	const [sluttAar, setSluttAar] = useState(new Date())
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
									<Yearpicker
										formMethods={formMethods}
										name={`${path}.startAar`}
										label="Startår"
										date={startAar}
										handleDateChange={(val) => {
											const time = val ? new Date(val) : null
											setStartAar(time)
											formMethods.setValue(`${path}.startAar`, val ? new Date(val) : null, {
												shouldTouch: true,
											})
											formMethods.trigger(`${path}.startAar`)
											formMethods.trigger(`${path}.sluttAar`)
										}}
										maxDate={new Date()}
									/>
									<Yearpicker
										formMethods={formMethods}
										name={`${path}.sluttAar`}
										label="Sluttår"
										date={sluttAar}
										handleDateChange={(val) => {
											const time = val ? new Date(val) : null
											setSluttAar(time)
											formMethods.setValue(`${path}.sluttAar`, time, { shouldTouch: true })
											formMethods.trigger(`${path}.sluttAar`)
											formMethods.trigger(`${path}.startAar`)
										}}
										maxDate={new Date()}
									/>
									<FormDateTimepicker
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
					startAar: testDatoFom(requiredDate.nullable(), 'sluttAar', 'Startår må være før sluttår'),
					sluttAar: testDatoTom(
						requiredDate.nullable(),
						'startAar',
						'Sluttår må være etter startår',
					),
				}),
			),
		}),
	),
}
