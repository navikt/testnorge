import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import React, { SyntheticEvent } from 'react'
import { SoekKategori } from '@/components/ui/soekForm/SoekForm'
import { personPath } from '@/pages/dollySoek/SoekForm'

export const Annet = ({ handleChange }: any) => {
	return (
		<SoekKategori>
			<FormCheckbox
				name={`${personPath}.harKontaktinformasjonForDoedsbo`}
				label="Har kontaktinformasjon for dødsbo"
				onChange={(val: SyntheticEvent) =>
					handleChange(val.target.checked, 'harKontaktinformasjonForDoedsbo')
				}
			/>
			<FormCheckbox
				name={`${personPath}.harOpphold`}
				label="Har opphold"
				onChange={(val: SyntheticEvent) => handleChange(val.target.checked, 'harOpphold')}
			/>
		</SoekKategori>
	)
}
