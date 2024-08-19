import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { getYearRangeOptions } from '@/utils/DataFormatter'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import React from 'react'
import { pensjonPath } from '@/components/fagsystem/pensjon/form/Form'

export const FyllInnInntektForm = ({ syttenFraOgMedAar }) => {
	return (
		<Kategori title="Pensjonsgivende inntekt" vis={pensjonPath}>
			<div className="flexbox--flex-wrap">
				<FormSelect
					name={`${pensjonPath}.fomAar`}
					label="Fra og med år"
					options={getYearRangeOptions(syttenFraOgMedAar || 1968, new Date().getFullYear() - 1)}
					size={'xsmall'}
					isClearable={false}
				/>

				<FormSelect
					name={`${pensjonPath}.tomAar`}
					label="Til og med år"
					options={getYearRangeOptions(1968, new Date().getFullYear() - 1)}
					size={'xsmall'}
					isClearable={false}
				/>

				<FormTextInput name={`${pensjonPath}.belop`} label="Beløp" type="number" />

				<FormCheckbox
					name={`${pensjonPath}.redusertMedGrunnbelop`}
					label="Nedjuster med grunnbeløp"
					size="small"
					checkboxMargin
				/>
			</div>
		</Kategori>
	)
}
