import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import React, { SyntheticEvent } from 'react'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { SoekKategori } from '@/components/ui/soekForm/SoekForm'
import { personPath } from '@/pages/dollySoek/SoekForm'
import { useFormContext } from 'react-hook-form'

export const Identifikasjon = ({ handleChange }: any) => {
	const formMethods = useFormContext()

	return (
		<SoekKategori>
			<FormTextInput
				name={`${personPath}.ident`}
				placeholder="Skriv inn ident ..."
				size="large"
				value={formMethods.watch(`${personPath}.ident`)}
				onBlur={(val: SyntheticEvent) => handleChange(val?.target?.value || null, 'ident')}
			/>
			<div style={{ marginLeft: '-20px', marginRight: '20px', paddingTop: '5px' }}>
				<Hjelpetekst>
					Søk på ident gir kun søk på hovedperson og ikke evt. relaterte personer.
				</Hjelpetekst>
			</div>
			<FormSelect
				name={`${personPath}.identtype`}
				options={Options('identtype')}
				size="small"
				placeholder="Velg identtype ..."
				onChange={(val: SyntheticEvent) => handleChange(val?.value || null, 'identtype')}
			/>
			<FormCheckbox
				name={`${personPath}.harFalskIdentitet`}
				label="Har falsk identitet"
				onChange={(val: SyntheticEvent) => handleChange(val.target.checked, 'harFalskIdentitet')}
			/>
			<FormCheckbox
				name={`${personPath}.harUtenlandskIdentifikasjonsnummer`}
				label="Har utenlandsk identitet"
				onChange={(val: SyntheticEvent) =>
					handleChange(val.target.checked, 'harUtenlandskIdentifikasjonsnummer')
				}
			/>
			<FormCheckbox
				name={`${personPath}.harNyIdentitet`}
				label="Har ny identitet"
				onChange={(val: SyntheticEvent) => handleChange(val.target.checked, 'harNyIdentitet')}
			/>
		</SoekKategori>
	)
}
