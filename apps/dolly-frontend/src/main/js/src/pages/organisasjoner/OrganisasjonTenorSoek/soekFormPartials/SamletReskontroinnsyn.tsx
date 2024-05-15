import { SoekKategori } from '@/components/ui/soekForm/SoekForm'
import React from 'react'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'

export const SamletReskontroinnsyn = ({ handleChange }: any) => (
	<SoekKategori>
		<h4>Reskontrooppslag for krav og betalinger</h4>
		<FormCheckbox
			name="tenorRelasjoner.samletReskontroinnsyn.harKrav"
			label="Har krav"
			onChange={(val: any) =>
				handleChange(
					val?.target?.checked || undefined,
					'tenorRelasjoner.samletReskontroinnsyn.harKrav',
				)
			}
		/>
		<FormCheckbox
			name="tenorRelasjoner.samletReskontroinnsyn.harInnbetaling"
			label="Har innbetaling"
			onChange={(val: any) =>
				handleChange(
					val?.target?.checked || undefined,
					'tenorRelasjoner.samletReskontroinnsyn.harInnbetaling',
				)
			}
		/>
	</SoekKategori>
)
