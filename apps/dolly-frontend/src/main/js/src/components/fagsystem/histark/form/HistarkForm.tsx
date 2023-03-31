import React, { useEffect, useState } from 'react'
import * as Yup from 'yup'
import { ifPresent, requiredDate, requiredString } from '@/utils/YupValidations'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { FormikProps } from 'formik'
import { DokumentInfoListe } from '@/components/fagsystem/dokarkiv/modal/DokumentInfoListe'
import { Vedlegg } from '@/components/fagsystem/dokarkiv/form/DokarkivForm'
import { FileUploader } from '@/utils/FileUploader/FileUploader'
import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { initialHistark } from '@/components/fagsystem/arbeidsplassen/form/initialValues'
import { useNavEnheter } from '@/utils/hooks/useNorg2'
import * as _ from 'lodash'
import { Option } from '@/service/SelectOptionsOppslag'
import { FormikDateTimepicker } from '@/components/ui/form/inputs/timepicker/Timepicker'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { Yearpicker } from '@/components/ui/form/inputs/yearpicker/Yearpicker'

interface HistarkFormProps {
	formikBag: FormikProps<object>
}

type Skjema = {
	data: string
	label: string
	lowercaseLabel: string
	value: string
}

enum Kodeverk {
	TEMA = 'Tema',
}

export const histarkAttributt = 'histark'

export const HistarkForm = ({ formikBag }: HistarkFormProps) => {
	if (!_.has(formikBag.values, histarkAttributt)) {
		return null
	}

	const sessionDokumenter = _.get(formikBag.values, 'histark.vedlegg')
	const [files, setFiles] = useState(sessionDokumenter || [])
	const [startAar, setStartAar] = useState(new Date())
	const [sluttAar, setSluttAar] = useState(new Date())
	const [selectedNavEnhet, setSelectedNavEnhet] = useState({} as Option)
	const [skjemaValues, setSkjemaValues] = useState(_.get(formikBag.values, 'histark.skjema'))

	const { navEnheter = [] } = useNavEnheter()

	useEffect(() => {
		handleVedleggChange(files)
	}, [files, skjemaValues])
	const handleVedleggChange = (filer: [Vedlegg]) => {
		setFiles(filer)
		formikBag.setFieldValue('histark.vedlegg', filer)
		filer.forEach((fil: Vedlegg, index: number) => {
			formikBag.setFieldValue(`histark.dokumenter.${index}.tittel`, fil.dokNavn || fil.name)
			formikBag.setFieldValue(`histark.dokumenter.${index}.antallSider`, 1)
			formikBag.setFieldValue(`histark.dokumenter.${index}.fysiskDokument`, fil.content.base64)
		})
	}

	return (
		// @ts-ignore
		<Vis attributt={histarkAttributt}>
			<Panel
				heading="Dokumenter (histark)"
				hasErrors={panelError(formikBag, histarkAttributt)}
				iconType="dokarkiv"
				// @ts-ignore
				startOpen={erForsteEllerTest(formikBag.values, [histarkAttributt])}
			>
				<Kategori title={`Oppretting av saksmappe for histark`} vis={histarkAttributt}>
					<FormikDollyFieldArray
						name="histark.dokumenter"
						header="Dokumenter"
						newEntry={initialHistark}
						maxEntries={1} //Foreløpig er bare 1 innsending støttet, backend har mulighet for flere
						canBeEmpty={false}
					>
						{(path: string) => (
							<div className="flexbox--column">
								<div className="flexbox--flex-wrap">
									<FormikSelect
										name={`${path}.temakoder`}
										label="Temakoder"
										kodeverk={Kodeverk.TEMA}
										size="xlarge"
										isClearable={false}
										isMulti={true}
									/>
									<FormikSelect
										name={'navenhet'}
										value={selectedNavEnhet?.value}
										onChange={(selected: Option) => {
											formikBag.setFieldValue(`${path}.enhetsnummer`, selected.value)
											formikBag.setFieldValue(`${path}.enhetsnavn`, selected.label)
											setSelectedNavEnhet(selected)
										}}
										label={'NAV-enhet'}
										fastfield={false}
										size={'xlarge'}
										options={navEnheter}
										feil={
											_.has(formikBag.errors, `${path}.enhetsnavn`)
												? { feilmelding: 'Velg en NAV-enhet' }
												: null
										}
										isLoading={_.isEmpty(navEnheter)}
									/>
									<Yearpicker
										formikBag={formikBag}
										name={`${path}.startAar`}
										label="Startår"
										date={startAar}
										handleDateChange={(val) =>
											formikBag.setFieldValue(`${path}.startAar`, val ? new Date(val) : null)
										}
										maxDate={new Date()}
									/>
									<Yearpicker
										formikBag={formikBag}
										name={`${path}.sluttAar`}
										label="Sluttår"
										date={sluttAar}
										handleDateChange={(val) => {
											const time = val ? new Date(val) : null
											setSluttAar(time)
											formikBag.setFieldValue(`${path}.sluttAar`, time)
										}}
										maxDate={new Date()}
									/>
									<FormikDateTimepicker
										name={`${path}.skanningsTidspunkt`}
										label="Skanningstidspunkt"
										visHvisAvhuket={false}
										size="medium"
									/>
									<FormikTextInput
										name={`${path}.skanner`}
										label="Skanner"
										visHvisAvhuket={false}
										size={'xsmall'}
									/>
									<FormikTextInput
										name={`${path}.skannested`}
										label="Skannested"
										visHvisAvhuket={false}
										size={'xsmall'}
									/>
									<Kategori title={'Vedlegg'}>
										<FileUploader
											files={files}
											setFiles={setFiles}
											feil={
												_.has(formikBag.errors, `${path}.tittel`)
													? { feilmelding: 'Fil er påkrevd' }
													: null
											}
										/>
										{files.length > 0 && (
											<DokumentInfoListe handleChange={handleVedleggChange} filer={files} />
										)}
									</Kategori>
								</div>
							</div>
						)}
					</FormikDollyFieldArray>
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
					enhetsnavn: requiredString,
					enhetsnummer: requiredString,
					skanner: requiredString,
					skannested: requiredString,
					skanningsTidspunkt: requiredDate.nullable().required(),
					startAar: requiredDate.nullable().required(),
					sluttAar: requiredDate.nullable().required(),
				})
			),
		})
	),
}
