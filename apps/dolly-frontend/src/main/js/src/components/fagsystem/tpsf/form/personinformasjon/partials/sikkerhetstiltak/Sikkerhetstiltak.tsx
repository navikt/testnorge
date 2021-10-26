import * as React from 'react'
import { FormikProps } from 'formik'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { DollySelect, FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import _get from 'lodash/get'

interface SikkerhetstiltakValues {
	typeSikkerhetTiltak: string
	beskrSikkerhetTiltak: string
	sikkerhetTiltakDatoFom: string
	sikkerhetTiltakDatoTom: string
}

interface SikkerhetstiltakProps {
	formikBag: FormikProps<{ tpsf: SikkerhetstiltakValues }>
}

type Option = {
	value: string
	label: string
}

export const Sikkerhetstiltak = ({ formikBag }: SikkerhetstiltakProps) => {
	const paths = {
		typeSikkerhetTiltak: 'tpsf.typeSikkerhetTiltak',
		beskrSikkerhetTiltak: 'tpsf.beskrSikkerhetTiltak',
		sikkerhetTiltakDatoFom: 'tpsf.sikkerhetTiltakDatoFom',
		sikkerhetTiltakDatoTom: 'tpsf.sikkerhetTiltakDatoTom',
	}

	const handleSikkerhetstiltakChange = (option: Option) => {
		formikBag.setFieldValue(paths.typeSikkerhetTiltak, option.value)
		formikBag.setFieldValue(paths.beskrSikkerhetTiltak, option.label)
	}

	return (
		<Vis attributt={Object.values(paths)} formik>
			<div className="flexbox--flex-wrap">
				<DollySelect
					name={paths.typeSikkerhetTiltak}
					label="Type sikkerhetstiltak"
					options={Options('sikkerhetstiltakType')}
					size="large"
					onChange={handleSikkerhetstiltakChange}
					value={_get(formikBag.values, paths.typeSikkerhetTiltak)}
					isClearable={false}
					// TODO: Vise value i label???
					// TODO: Validering - alt er påkrevd
				/>
				<FormikDatepicker name={paths.sikkerhetTiltakDatoFom} label="Sikkerhetstiltak starter" />
				<FormikDatepicker name={paths.sikkerhetTiltakDatoTom} label="Sikkerhetstiltak opphører" />
			</div>
		</Vis>
	)
}
