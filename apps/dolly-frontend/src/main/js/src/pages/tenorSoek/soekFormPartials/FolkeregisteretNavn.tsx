import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import React, { SyntheticEvent } from 'react'
import { SoekKategori } from '@/components/ui/soekForm/SoekForm'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormikCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'

export const FolkeregisteretNavn = ({ handleChange, handleChangeTextInput }: any) => {
	return (
		<SoekKategori>
			<FormikTextInput
				name="navn.navnLengde.fraOgMed"
				label="Lengde på navn f.o.m."
				type="number"
				onBlur={(val: SyntheticEvent) =>
					handleChange(val?.target?.value || null, 'navn.navnLengde.fraOgMed')
				}
				visHvisAvhuket={false}
			/>
			<FormikTextInput
				name="navn.navnLengde.tilOgMed"
				label="Lengde på navn t.o.m."
				type="number"
				onBlur={(val: SyntheticEvent) =>
					handleChange(val?.target?.value || null, 'navn.navnLengde.tilOgMed')
				}
				visHvisAvhuket={false}
			/>
			<FormikSelect
				name="navn.harFlereFornavn"
				options={Options('boolean')}
				label="Har flere fornnavn"
				onChange={(val: boolean) => handleChange(val?.value, 'navn.harFlereFornavn')}
			/>
			<FormikSelect
				name="navn.harNavnSpesialtegn"
				options={Options('boolean')}
				label="Har spesialtegn i navn"
				onChange={(val: boolean) => handleChange(val?.value, 'navn.harNavnSpesialtegn')}
			/>
			<FormikCheckbox
				name="navn.harMellomnavn"
				label="Har mellomnavn"
				onChange={(val: SyntheticEvent) =>
					handleChange(val?.target?.checked || undefined, 'navn.harMellomnavn')
				}
			/>
		</SoekKategori>
	)
}
