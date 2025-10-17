import { SoekKategori } from '@/components/ui/soekForm/SoekFormWrapper'
import React from 'react'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { useTenorDomain } from '@/utils/hooks/useTenorSoek'
import { createOptions } from '@/pages/tenorSoek/utils'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { AdresseKodeverk } from '@/config/kodeverk'
import { Option } from '@/service/SelectOptionsOppslag'
import { codeToNorskLabel, oversettBoolean } from '@/utils/DataFormatter'

export const FolkeregisteretAdresse = ({ handleChange }: any) => {
	const { domain: adresseGraderingOptions, loading: loadingAdresseGradering } =
		useTenorDomain('AdresseGradering')

	return (
		<SoekKategori>
			<FormSelect
				name="adresser.adresseGradering"
				options={createOptions(adresseGraderingOptions?.data)}
				label="Adressegradering"
				onChange={(val: any) =>
					handleChange(
						val?.value || null,
						'adresser.adresseGradering',
						`Adressegradering: ${codeToNorskLabel(val?.value)}`,
					)
				}
				isLoading={loadingAdresseGradering}
			/>
			<FormSelect
				name="adresser.kommunenummer"
				label="Kommunenummer"
				kodeverk={AdresseKodeverk.Kommunenummer}
				onChange={(val: Option) =>
					handleChange(val?.value, 'adresser.kommunenummer', `Kommunenummer: ${val?.value}`)
				}
				size="large"
			/>
			<FormSelect
				name="adresser.harAdresseSpesialtegn"
				options={Options('boolean')}
				label="Har spesialtegn i adresse"
				onChange={(val: any) =>
					handleChange(
						val?.value,
						'adresser.harAdresseSpesialtegn',
						`Har spesialtegn i adresse: ${oversettBoolean(val?.value)}`,
					)
				}
			/>
			<div className="flexbox--flex-wrap">
				<FormCheckbox
					name="adresser.harBostedsadresse"
					label="Har bostedsadresse"
					onChange={(val: any) =>
						handleChange(
							val?.target?.checked || undefined,
							'adresser.harBostedsadresse',
							'Har bostedsadresse',
						)
					}
				/>
				<FormCheckbox
					name="avansert.harBostedsadresseHistorikk"
					label="Har bostedsadressehistorikk"
					onChange={(val: any) =>
						handleChange(
							val?.target?.checked || undefined,
							'avansert.harBostedsadresseHistorikk',
							'Har bostedsadressehistorikk',
						)
					}
				/>
				<FormCheckbox
					name="adresser.harOppholdAnnetSted"
					label="Har opphold annet sted"
					onChange={(val: any) =>
						handleChange(
							val?.target?.checked || undefined,
							'adresser.harOppholdAnnetSted',
							'Har opphold annet sted',
						)
					}
				/>
				<FormCheckbox
					name="adresser.harPostadresseNorge"
					label="Har postadresse i Norge"
					onChange={(val: any) =>
						handleChange(
							val?.target?.checked || undefined,
							'adresser.harPostadresseNorge',
							'Har postadresse i Norge',
						)
					}
				/>
				<FormCheckbox
					name="adresser.harPostadresseUtland"
					label="Har postadresse i utlandet"
					onChange={(val: any) =>
						handleChange(
							val?.target?.checked || undefined,
							'adresser.harPostadresseUtland',
							'Har postadresse i utlandet',
						)
					}
				/>
				<FormCheckbox
					name="adresser.harKontaktadresseDoedsbo"
					label="Har kontaktadresse for dødsbo"
					onChange={(val: any) =>
						handleChange(
							val?.target?.checked || undefined,
							'adresser.harKontaktadresseDoedsbo',
							'Har kontaktadresse for dødsbo',
						)
					}
				/>
			</div>
		</SoekKategori>
	)
}
