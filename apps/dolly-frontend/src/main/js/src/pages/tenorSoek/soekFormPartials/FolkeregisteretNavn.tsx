import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import React from 'react'
import { SoekKategori } from '@/components/ui/soekForm/SoekForm'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { oversettBoolean } from '@/utils/DataFormatter'

export const FolkeregisteretNavn = ({ handleChange }: any) => {
	return (
		<SoekKategori>
			<FormTextInput
				name="navn.navnLengde.fraOgMed"
				label="Lengde på navn f.o.m."
				type="number"
				// @ts-ignore
				onBlur={(val: any) =>
					handleChange(
						val?.target?.value || null,
						'navn.navnLengde.fraOgMed',
						`Lengde på navn f.o.m.: ${val?.target?.value}`,
					)
				}
				visHvisAvhuket={false}
			/>
			<FormTextInput
				name="navn.navnLengde.tilOgMed"
				label="Lengde på navn t.o.m."
				type="number"
				// @ts-ignore
				onBlur={(val: any) =>
					handleChange(
						val?.target?.value || null,
						'navn.navnLengde.tilOgMed',
						`Lengde på navn t.o.m.: ${val?.target?.value}`,
					)
				}
				visHvisAvhuket={false}
			/>
			<FormSelect
				name="navn.harFlereFornavn"
				options={Options('boolean')}
				label="Har flere fornavn"
				onChange={(val: any) =>
					handleChange(
						val?.value,
						'navn.harFlereFornavn',
						`Har flere fornavn: ${oversettBoolean(val?.value)}`,
					)
				}
			/>
			{/*TODO: Denne feiler naar man gjoer soek med den:*/}
			<FormSelect
				name="navn.harNavnSpesialtegn"
				options={Options('boolean')}
				label="Har spesialtegn i navn"
				onChange={(val: any) =>
					handleChange(
						val?.value,
						'navn.harNavnSpesialtegn',
						`Har spesialtegn i navn: ${oversettBoolean(val?.value)}`,
					)
				}
			/>
			<FormCheckbox
				name="navn.harMellomnavn"
				label="Har mellomnavn"
				onChange={(val: any) =>
					handleChange(val?.target?.checked || undefined, 'navn.harMellomnavn', 'Har mellomnavn')
				}
			/>
		</SoekKategori>
	)
}
