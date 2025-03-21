import { useTenorDomain } from '@/utils/hooks/useTenorSoek'
import { SoekKategori } from '@/components/ui/soekForm/SoekForm'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { createOptions } from '@/pages/tenorSoek/utils'
import { SyntheticEvent } from 'react'

export const Arbeidsforhold = ({ handleChange, handleChangeList, getValue }: any) => {
	const { domain: arbeidsforholdstypeOptions } = useTenorDomain('Arbeidsforholdstype')

	return (
		<SoekKategori>
			<FormSelect
				name="arbeidsforhold.arbeidsforholdstype"
				options={createOptions(arbeidsforholdstypeOptions?.data)}
				size="xlarge"
				label="Arbeidsforholdstype"
				onChange={(val: SyntheticEvent) =>
					handleChange(val?.value || null, 'arbeidsforhold.arbeidsforholdstype')
				}
			/>
		</SoekKategori>
	)
}
