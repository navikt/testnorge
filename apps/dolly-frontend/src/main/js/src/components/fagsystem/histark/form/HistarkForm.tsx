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
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormikDateTimepicker } from '@/components/ui/form/inputs/timepicker/Timepicker'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'

interface HistarkFormProps {
	formikBag: FormikProps<{}>
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
	const [selectedNavEnhet, setSelectedNavEnhet] = useState({} as Option)
	const [skjemaValues, setSkjemaValues] = useState(_.get(formikBag.values, 'histark.skjema'))

	const { navEnheter = [] } = useNavEnheter()

	useEffect(() => {
		handleVedleggChange(files, null)
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
				heading="Dokumenter"
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
						canBeEmpty={false}
					>
						{(path: string, idx: number) => (
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
										label={'NAV enhet'}
										fastfield={false}
										size={'xlarge'}
										options={navEnheter}
										isLoading={_.isEmpty(navEnheter)}
									/>
									<FormikDatepicker
										name={`${path}.startAar`}
										label="Startår"
										visHvisAvhuket={false}
										size="medium"
									/>
									<FormikDatepicker
										name={`${path}.sluttAar`}
										label="Sluttår"
										visHvisAvhuket={false}
										size="medium"
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
									/>
									<FormikTextInput
										name={`${path}.skannested`}
										label="Skannested"
										visHvisAvhuket={false}
									/>
									<FileUploader files={files} setFiles={setFiles} />
									{files.length > 0 && (
										<DokumentInfoListe handleChange={handleVedleggChange} filer={files} />
									)}
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
					temakoder: Yup.array().required().of(Yup.string()),
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
