import { SoekKategori } from '@/components/ui/soekForm/SoekForm'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import React from 'react'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { useTenorDomain } from '@/utils/hooks/useTenorSoek'
import { createOptions } from '@/pages/tenorSoek/utils'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'

export const FolkeregisteretAdresse = ({ handleChange }: any) => {
	const { domain: adresseGraderingOptions } = useTenorDomain('AdresseGradering')

	return (
		<SoekKategori>
			<FormSelect
				name="adresser.adresseGradering"
				options={createOptions(adresseGraderingOptions?.data)}
				label="Adressegradering"
				onChange={(val: any) => handleChange(val?.value || null, 'adresser.adresseGradering')}
			/>
			<FormTextInput
				name="adresser.kommunenummer"
				label="Kommunenummer"
				type="number"
				// @ts-ignore
				onBlur={(val: any) => handleChange(val?.target?.value || null, 'adresser.kommunenummer')}
				visHvisAvhuket={false}
			/>
			<FormSelect
				name="adresser.harAdresseSpesialtegn"
				options={Options('boolean')}
				label="Har spesialtegn i adresse"
				onChange={(val: any) => handleChange(val?.value, 'adresser.harAdresseSpesialtegn')}
			/>
			<div className="flexbox--flex-wrap">
				<FormCheckbox
					name="adresser.harBostedsadresse"
					label="Har bostedsadresse"
					onChange={(val: any) =>
						handleChange(val?.target?.checked || undefined, 'adresser.harBostedsadresse')
					}
				/>
				<FormCheckbox
					name="adresser.harOppholdAnnetSted"
					label="Har opphold annet sted"
					onChange={(val: any) =>
						handleChange(val?.target?.checked || undefined, 'adresser.harOppholdAnnetSted')
					}
				/>
				<FormCheckbox
					name="adresser.harPostadresseNorge"
					label="Har postadresse i Norge"
					onChange={(val: any) =>
						handleChange(val?.target?.checked || undefined, 'adresser.harPostadresseNorge')
					}
				/>
				<FormCheckbox
					name="adresser.harPostadresseUtland"
					label="Har postadresse i utlandet"
					onChange={(val: any) =>
						handleChange(val?.target?.checked || undefined, 'adresser.harPostadresseUtland')
					}
				/>
				<FormCheckbox
					name="adresser.harKontaktadresseDoedsbo"
					label="Har kontaktadresse for dødsbo"
					onChange={(val: any) =>
						handleChange(val?.target?.checked || undefined, 'adresser.harKontaktadresseDoedsbo')
					}
				/>
			</div>
		</SoekKategori>
	)
}
