import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import React, { SyntheticEvent } from 'react'
import { SoekKategori } from '@/components/ui/soekForm/SoekForm'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'

export const FolkeregisteretNavn = ({ formikBag, handleChange, getValue }: any) => {
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
				// fastfield={false}
			/>
			<FormikTextInput
				name="navn.navnLengde.tilOgMed"
				label="Lengde på navn t.o.m."
				type="number"
				onBlur={(val: SyntheticEvent) =>
					handleChange(val?.target?.value || null, 'navn.navnLengde.tilOgMed')
				}
				visHvisAvhuket={false}
				// fastfield={false}
			/>
			<FormikSelect
				name="navn.harFlereFornavn"
				options={Options('boolean')}
				size="small"
				label="Har flere fornnavn"
				onChange={(val: boolean) => handleChange(val?.value, 'navn.harFlereFornavn')}
				value={getValue('navn.harFlereFornavn')}
			/>
			<FormikSelect
				name="navn.harMellomnavn"
				options={Options('boolean')}
				size="small"
				label="Har mellomnavn"
				onChange={(val: boolean) => handleChange(val?.value, 'navn.harMellomnavn')}
				value={getValue('navn.harMellomnavn')}
			/>
			<FormikSelect
				name="navn.harNavnSpesialtegn"
				options={Options('boolean')}
				size="small"
				label="Har spesialtegn i navn"
				onChange={(val: boolean) => handleChange(val?.value, 'navn.harNavnSpesialtegn')}
				value={getValue('navn.harNavnSpesialtegn')}
			/>
		</SoekKategori>
	)
}
