import { DollyCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import React, { useState } from 'react'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import {
	barnetilleggDetaljer,
	forventedeInntekterSokerOgEP,
} from '@/components/fagsystem/uforetrygd/initialValues'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'

const ForventedeInntekterForm = ({ path, header, initialValues }) => {
	return (
		<FormDollyFieldArray name={path} header={header} newEntry={initialValues} nested>
			{(path: any, idx: React.Key) => (
				<>
					<FormSelect
						name={`${path}.inntektType`}
						label="Type inntekt"
						size="xlarge"
						options={Options('inntektType')}
					/>
					<FormTextInput name={`${path}.belop`} label="Beløp" type="number" size="medium" />
					<FormDatepicker name={`${path}.datoFom`} label="Dato f.o.m." />
					<FormDatepicker name={`${path}.datoTom`} label="Dato t.o.m." />
				</>
			)}
		</FormDollyFieldArray>
	)
}

export const BarnetilleggForm = ({ formMethods }) => {
	const barnetilleggPath = 'pensjonforvalter.uforetrygd.barnetilleggDetaljer'

	const [harBarnetillegg, setHarBarnetillegg] = useState(
		formMethods.watch(barnetilleggPath) !== null,
	)

	const handleBarnetilleggChange = (value) => {
		const checked = value?.target?.checked
		setHarBarnetillegg(checked)
		if (checked) {
			formMethods.setValue(barnetilleggPath, barnetilleggDetaljer)
		} else {
			formMethods.setValue(barnetilleggPath, null)
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
					<FormSelect
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
