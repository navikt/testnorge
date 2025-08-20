import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import React, { SyntheticEvent } from 'react'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { SoekKategori } from '@/components/ui/soekForm/SoekFormWrapper'
import { adressePath, personPath } from '@/pages/dollySoek/SoekForm'

export const Familierelasjoner = ({ handleChange, handleChangeAdresse }: any) => {
	return (
		<SoekKategori>
			<FormSelect
				name={`${personPath}.sivilstand`}
				options={Options('sivilstandType')?.filter((item) => item.value !== 'SAMBOER')}
				size="large"
				placeholder="Velg sivilstand ..."
				onChange={(val: SyntheticEvent) => handleChange(val?.value || null, 'sivilstand')}
			/>
			<FormCheckbox
				name={`${personPath}.harBarn`}
				label="Har barn"
				onChange={(val: SyntheticEvent) => handleChange(val.target.checked, 'harBarn')}
			/>
			<FormCheckbox
				name={`${personPath}.harForeldre`}
				label="Har foreldre"
				onChange={(val: SyntheticEvent) => handleChange(val.target.checked, 'harForeldre')}
			/>
			<FormCheckbox
				name={`${personPath}.harDoedfoedtBarn`}
				label="Har dødfødt barn"
				onChange={(val: SyntheticEvent) => handleChange(val.target.checked, 'harDoedfoedtBarn')}
			/>
			<FormCheckbox
				name={`${personPath}.harForeldreAnsvar`}
				label="Har foreldreansvar"
				onChange={(val: SyntheticEvent) => handleChange(val.target.checked, 'harForeldreAnsvar')}
			/>
			<FormCheckbox
				name={`${adressePath}.harDeltBosted`}
				label="Har delt bosted"
				onChange={(val: SyntheticEvent) => handleChangeAdresse(val.target.checked, 'harDeltBosted')}
			/>
		</SoekKategori>
	)
}
