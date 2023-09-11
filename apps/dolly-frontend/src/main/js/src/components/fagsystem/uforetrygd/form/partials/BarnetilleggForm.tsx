import { DollyCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import React, { useState } from 'react'
import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import * as _ from 'lodash-es'
import {
	barnetilleggDetaljer,
	forventedeInntekterSokerOgEP,
} from '@/components/fagsystem/uforetrygd/initialValues'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'

const ForventedeInntekterForm = ({ path, header, initialValues }) => {
	return (
		<FormikDollyFieldArray name={path} header={header} newEntry={initialValues} nested>
			{(path: any, idx: React.Key) => (
				<>
					<FormikSelect
						name={`${path}.inntektType`}
						label="Type inntekt"
						size="xlarge"
						options={Options('inntektType')}
					/>
					<FormikTextInput
						name={`${path}.belop`}
						label="Beløp"
						type="number"
						size="medium"
						fastfield="false"
					/>
					<FormikDatepicker name={`${path}.datoFom`} label="Dato f.o.m." />
					<FormikDatepicker name={`${path}.datoTom`} label="Dato t.o.m." />
				</>
			)}
		</FormikDollyFieldArray>
	)
}

export const BarnetilleggForm = ({ formikBag }) => {
	const barnetilleggPath = 'pensjonforvalter.uforetrygd.barnetilleggDetaljer'

	const [harBarnetillegg, setHarBarnetillegg] = useState(
		_.get(formikBag.values, barnetilleggPath) !== null,
	)

	const handleBarnetilleggChange = (value) => {
		const checked = value?.target?.checked
		setHarBarnetillegg(checked)
		if (checked) {
			formikBag.setFieldValue(barnetilleggPath, barnetilleggDetaljer)
		} else {
			formikBag.setFieldValue(barnetilleggPath, null)
		}
	}

	return (
		<>
			<DollyCheckbox
				label="Har barnetillegg"
				id={barnetilleggPath}
				checked={harBarnetillegg}
				onChange={(v) => handleBarnetilleggChange(v)}
				size="small"
			/>
			{harBarnetillegg && (
				<div className="flexbox--flex-wrap" style={{ marginBottom: '15px' }}>
					<FormikSelect
						name={`${barnetilleggPath}.barnetilleggType`}
						label="Type barnetillegg"
						options={Options('barnetilleggType')}
						isClearable={false}
					/>
					<ForventedeInntekterForm
						header="Forventede inntekter for søker"
						path={`${barnetilleggPath}.forventedeInntekterSoker`}
						initialValues={forventedeInntekterSokerOgEP}
					/>
					<ForventedeInntekterForm
						header="Forventede inntekter for partner"
						path={`${barnetilleggPath}.forventedeInntekterEP`}
						initialValues={forventedeInntekterSokerOgEP}
					/>
				</div>
			)}
		</>
	)
}
