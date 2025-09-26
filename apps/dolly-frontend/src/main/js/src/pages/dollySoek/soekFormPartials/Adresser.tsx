import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { AdresseKodeverk, GtKodeverk } from '@/config/kodeverk'
import React, { SyntheticEvent } from 'react'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { SoekKategori } from '@/components/ui/soekForm/SoekForm'
import { adressePath } from '../SoekForm'
import { codeToNorskLabel } from '@/utils/DataFormatter'

export const Adresser = ({ handleChange }: any) => {
	return (
		<SoekKategori>
			<div className="flexbox--full-width">
				<div className="flexbox--flex-wrap">
					<FormSelect
						name={`${adressePath}.kommunenummer`}
						kodeverk={AdresseKodeverk.Kommunenummer}
						size="large"
						placeholder="Velg kommunenummer ..."
						onChange={(val: SyntheticEvent) =>
							handleChange(
								val?.value || null,
								`${adressePath}.kommunenummer`,
								`Kommunenummer: ${val?.value}`,
							)
						}
					/>
					<FormSelect
						name={`${adressePath}.postnummer`}
						kodeverk={AdresseKodeverk.Postnummer}
						size="large"
						placeholder="Velg postnummer ..."
						onChange={(val: SyntheticEvent) =>
							handleChange(
								val?.value || null,
								`${adressePath}.postnummer`,
								`Postnummer: ${val?.value}`,
							)
						}
					/>
					<FormSelect
						name={`${adressePath}.bydelsnummer`}
						kodeverk={GtKodeverk.BYDEL}
						size="large"
						placeholder="Velg bydelsnummer ..."
						onChange={(val: SyntheticEvent) =>
							handleChange(
								val?.value || null,
								`${adressePath}.bydelsnummer`,
								`Bydelsnummer: ${val?.value}`,
							)
						}
					/>
					<FormSelect
						name={`${adressePath}.addressebeskyttelse`}
						options={Options('gradering')}
						size="xlarge"
						placeholder="Velg adressebeskyttelse (kode 6/7) ..."
						onChange={(val: SyntheticEvent) =>
							handleChange(
								val?.value || null,
								`${adressePath}.addressebeskyttelse`,
								`Adressebeskyttelse: ${codeToNorskLabel(val?.value)}`,
							)
						}
					/>
				</div>
			</div>
			<FormCheckbox
				name={`${adressePath}.harBostedsadresse`}
				label="Har bostedsadresse"
				onChange={(val: SyntheticEvent) =>
					handleChange(val.target.checked, `${adressePath}.harBostedsadresse`, 'Har bostedsadresse')
				}
			/>
			<FormCheckbox
				name={`${adressePath}.harOppholdsadresse`}
				label="Har oppholdsadresse"
				onChange={(val: SyntheticEvent) =>
					handleChange(
						val.target.checked,
						`${adressePath}.harOppholdsadresse`,
						'Har oppholdsadresse',
					)
				}
			/>
			<FormCheckbox
				name={`${adressePath}.harKontaktadresse`}
				label="Har kontaktadresse"
				onChange={(val: SyntheticEvent) =>
					handleChange(val.target.checked, `${adressePath}.harKontaktadresse`, 'Har kontaktadresse')
				}
			/>
			{/*//Soek paa matrikkeladresse fungerer for tiden ikke*/}
			{/*<FormCheckbox*/}
			{/*	name={`${adressePath}.harMatrikkelAdresse`}*/}
			{/*	label="Har matrikkeladresse"*/}
			{/*	onChange={(val: SyntheticEvent) =>*/}
			{/*		handleChangeAdresse(val.target.checked, 'harMatrikkelAdresse')*/}
			{/*	}*/}
			{/*/>*/}
			<FormCheckbox
				name={`${adressePath}.harUtenlandsadresse`}
				label="Har utenlandsadresse"
				onChange={(val: SyntheticEvent) =>
					handleChange(
						val.target.checked,
						`${adressePath}.harUtenlandsadresse`,
						'Har utenlandsadresse',
					)
				}
			/>
			<FormCheckbox
				name={`${adressePath}.harUkjentAdresse`}
				label="Har ukjent adresse"
				onChange={(val: SyntheticEvent) =>
					handleChange(val.target.checked, `${adressePath}.harUkjentAdresse`, 'Har ukjent adresse')
				}
			/>
			<FormCheckbox
				name={`${adressePath}.harBydelsnummer`}
				label="Har bydelsnummer"
				onChange={(val: SyntheticEvent) =>
					handleChange(val.target.checked, `${adressePath}.harBydelsnummer`, 'Har bydelsnummer')
				}
			/>
		</SoekKategori>
	)
}
