import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { AdresseKodeverk, GtKodeverk } from '@/config/kodeverk'
import React, { SyntheticEvent } from 'react'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { SoekKategori } from '@/components/ui/soekForm/SoekFormWrapper'
import { adressePath } from '../SoekForm'

export const Adresser = ({ handleChangeAdresse }: any) => {
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
							handleChangeAdresse(val?.value || null, 'kommunenummer')
						}
					/>
					<FormSelect
						name={`${adressePath}.postnummer`}
						kodeverk={AdresseKodeverk.Postnummer}
						size="large"
						placeholder="Velg postnummer ..."
						onChange={(val: SyntheticEvent) =>
							handleChangeAdresse(val?.value || null, 'postnummer')
						}
					/>
					<FormSelect
						name={`${adressePath}.bydelsnummer`}
						kodeverk={GtKodeverk.BYDEL}
						size="large"
						placeholder="Velg bydelsnummer ..."
						onChange={(val: SyntheticEvent) =>
							handleChangeAdresse(val?.value || null, 'bydelsnummer')
						}
					/>
					<FormSelect
						name={`${adressePath}.addressebeskyttelse`}
						options={Options('gradering')}
						size="xlarge"
						placeholder="Velg adressebeskyttelse (kode 6/7) ..."
						onChange={(val: SyntheticEvent) =>
							handleChangeAdresse(val?.value || null, 'addressebeskyttelse')
						}
					/>
				</div>
			</div>
			<FormCheckbox
				name={`${adressePath}.harBostedsadresse`}
				label="Har bostedsadresse"
				onChange={(val: SyntheticEvent) =>
					handleChangeAdresse(val.target.checked, 'harBostedsadresse')
				}
			/>
			<FormCheckbox
				name={`${adressePath}.harOppholdsadresse`}
				label="Har oppholdsadresse"
				onChange={(val: SyntheticEvent) =>
					handleChangeAdresse(val.target.checked, 'harOppholdsadresse')
				}
			/>
			<FormCheckbox
				name={`${adressePath}.harKontaktadresse`}
				label="Har kontaktadresse"
				onChange={(val: SyntheticEvent) =>
					handleChangeAdresse(val.target.checked, 'harKontaktadresse')
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
					handleChangeAdresse(val.target.checked, 'harUtenlandsadresse')
				}
			/>
			<FormCheckbox
				name={`${adressePath}.harUkjentAdresse`}
				label="Har ukjent adresse"
				onChange={(val: SyntheticEvent) =>
					handleChangeAdresse(val.target.checked, 'harUkjentAdresse')
				}
			/>
			<FormCheckbox
				name={`${adressePath}.harBydelsnummer`}
				label="Har bydelsnummer"
				onChange={(val: SyntheticEvent) =>
					handleChangeAdresse(val.target.checked, 'harBydelsnummer')
				}
			/>
		</SoekKategori>
	)
}
