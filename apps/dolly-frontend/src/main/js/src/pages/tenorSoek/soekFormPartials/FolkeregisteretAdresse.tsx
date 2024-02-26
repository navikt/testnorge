import { SoekKategori } from '@/components/ui/soekForm/SoekForm'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import React, { SyntheticEvent } from 'react'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { useTenorDomain } from '@/utils/hooks/useTenorSoek'
import { createOptions } from '@/pages/tenorSoek/utils'
import { FormikCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'

export const FolkeregisteretAdresse = ({ formikBag, handleChange, getValue }: any) => {
	const { domain: adresseGraderingOptions } = useTenorDomain('AdresseGradering')

	return (
		<SoekKategori>
			<FormikSelect
				name="adresser.adresseGradering"
				options={createOptions(adresseGraderingOptions?.data)}
				// size="medium"
				label="Adressegradering"
				onChange={(val: SyntheticEvent) =>
					handleChange(val?.value || null, 'adresser.adresseGradering')
				}
				value={getValue('adresser.adresseGradering')}
			/>
			<FormikTextInput
				name="adresser.kommunenummer"
				label="Kommunenummer"
				type="number"
				onBlur={(val: SyntheticEvent) =>
					handleChange(val?.target?.value || null, 'adresser.kommunenummer')
				}
				visHvisAvhuket={false}
				// fastfield={false}
			/>
			<FormikSelect
				name="adresser.harAdresseSpesialtegn"
				options={Options('boolean')}
				size="small"
				label="Har spesialtegn i adresse"
				onChange={(val: boolean) => handleChange(val?.value, 'adresser.harAdresseSpesialtegn')}
				value={getValue('adresser.harAdresseSpesialtegn')}
			/>
			<div className="flexbox--flex-wrap">
				<FormikCheckbox
					name="adresser.harBostedsadresse"
					label="Har bostedsadresse"
					onChange={(val: SyntheticEvent) =>
						handleChange(val?.target?.checked || undefined, 'adresser.harBostedsadresse')
					}
					value={getValue('adresser.harBostedsadresse')}
				/>
				<FormikCheckbox
					name="adresser.harOppholdAnnetSted"
					label="Har opphold annet sted"
					onChange={(val: SyntheticEvent) =>
						handleChange(val?.target?.checked || undefined, 'adresser.harOppholdAnnetSted')
					}
					value={getValue('adresser.harOppholdAnnetSted')}
				/>
				<FormikCheckbox
					name="adresser.harPostadresseNorge"
					label="Har postadresse i Norge"
					onChange={(val: SyntheticEvent) =>
						handleChange(val?.target?.checked || undefined, 'adresser.harPostadresseNorge')
					}
					value={getValue('adresser.harPostadresseNorge')}
				/>
				<FormikCheckbox
					name="adresser.harPostadresseUtland"
					label="Har postadresse i utlandet"
					onChange={(val: SyntheticEvent) =>
						handleChange(val?.target?.checked || undefined, 'adresser.harPostadresseUtland')
					}
					value={getValue('adresser.harPostadresseUtland')}
				/>
				<FormikCheckbox
					name="adresser.harKontaktadresseDoedsbo"
					label="Har kontaktadresse for dødsbo"
					onChange={(val: SyntheticEvent) =>
						handleChange(val?.target?.checked || undefined, 'adresser.harKontaktadresseDoedsbo')
					}
					value={getValue('adresser.harKontaktadresseDoedsbo')}
				/>
			</div>
		</SoekKategori>
	)
}