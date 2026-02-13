import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import React, { SyntheticEvent } from 'react'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { SoekKategori } from '@/components/ui/soekForm/SoekFormWrapper'
import { adressePath, personPath } from '@/pages/dollySoek/SoekForm'
import { codeToNorskLabel } from '@/utils/DataFormatter'

export const Familierelasjoner = ({ handleChange }: any) => {
	return (
		<SoekKategori>
			<FormSelect
				name={`${personPath}.sivilstand`}
				options={Options('sivilstandType')?.filter((item) => item.value !== 'SAMBOER')}
				size="large"
				placeholder="Velg sivilstand ..."
				onChange={(val: SyntheticEvent) =>
					handleChange(
						val?.value || null,
						`${personPath}.sivilstand`,
						`Sivilstand: ${codeToNorskLabel(val?.value)}`,
					)
				}
			/>
			<FormCheckbox
				name={`${personPath}.harBarn`}
				label="Har barn"
				onChange={(val: SyntheticEvent) =>
					handleChange(val.target.checked, `${personPath}.harBarn`, 'Har barn')
				}
			/>
			<FormCheckbox
				name={`${personPath}.harForeldre`}
				label="Har foreldre"
				onChange={(val: SyntheticEvent) =>
					handleChange(val.target.checked, `${personPath}.harForeldre`, 'Har foreldre')
				}
			/>
			<FormCheckbox
				name={`${personPath}.harDoedfoedtBarn`}
				label="Har dødfødt barn"
				onChange={(val: SyntheticEvent) =>
					handleChange(val.target.checked, `${personPath}.harDoedfoedtBarn`, 'Har dødfødt barn')
				}
			/>
			<FormCheckbox
				name={`${personPath}.harForeldreAnsvar`}
				label="Har foreldreansvar"
				onChange={(val: SyntheticEvent) =>
					handleChange(val.target.checked, `${personPath}.harForeldreAnsvar`, 'Har foreldreansvar')
				}
			/>
			<FormCheckbox
				name={`${adressePath}.harDeltBosted`}
				label="Har delt bosted"
				onChange={(val: SyntheticEvent) =>
					handleChange(val.target.checked, `${adressePath}.harDeltBosted`, 'Har delt bosted')
				}
			/>
			<FormTextInput
				name={`${personPath}.antallRelasjoner`}
				placeholder="Skriv inn antall relasjoner ..."
				type="number"
				onBlur={(val: SyntheticEvent) =>
					handleChange(
						val?.target?.value || null,
						`${personPath}.antallRelasjoner`,
						`Antall relasjoner: ${val?.target?.value}`,
					)
				}
			/>
		</SoekKategori>
	)
}
