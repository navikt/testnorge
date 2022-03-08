import React, { useEffect, useState } from 'react'
import _get from 'lodash/get'
import { FormikProps } from 'formik'
import { ToggleGruppe, ToggleKnapp } from '~/components/ui/toggle/Toggle'
import { OrganisasjonLoader } from '~/components/organisasjonSelect'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'
import { EgneOrganisasjoner } from '~/components/fagsystem/brregstub/form/partials/EgneOrganisasjoner'
import { MiljoeApi } from '~/service/Api'
import { OrganisasjonTextSelect } from '~/components/fagsystem/brregstub/form/partials/OrganisasjonTextSelect'

interface OrgnrToggleProps {
	path: string
	formikBag: FormikProps<{}>
	setEnhetsinfo: (org: any, path: string) => {}
}

const inputValg = { fraEgenListe: 'egen', fraFellesListe: 'felles', skrivSelv: 'skriv' }

export const OrgnrToggle = ({ path, formikBag, setEnhetsinfo }: OrgnrToggleProps) => {
	const [inputType, setInputType] = useState(inputValg.fraEgenListe)
	const [aktiveMiljoer, setAktiveMiljoer] = useState(null)

	useEffect(() => {
		const fetchData = async () => {
			const resp = await MiljoeApi.getAktiveMiljoer()
			setAktiveMiljoer(resp.data)
		}
		fetchData()
	}, [])

	const handleToggleChange = (event: React.ChangeEvent<any>) => {
		setInputType(event.target.value)
		clearEnhetsinfo()
	}

	const clearEnhetsinfo = () => {
		const oldValues = _get(formikBag.values, path)
		if (oldValues.hasOwnProperty('foretaksNavn')) delete oldValues['foretaksNavn']
		if (oldValues.hasOwnProperty('forretningsAdresse')) delete oldValues['forretningsAdresse']
		if (oldValues.hasOwnProperty('postAdresse')) delete oldValues['postAdresse']
		oldValues['orgNr'] = ''
		formikBag.setFieldValue(path, oldValues)
	}

	const handleChange = (event: React.ChangeEvent<any>) => {
		setEnhetsinfo(event, path)
	}

	return (
		<div className="toggle--wrapper">
			<ToggleGruppe onChange={handleToggleChange} name={path}>
				<ToggleKnapp
					key={inputValg.fraEgenListe}
					value={inputValg.fraEgenListe}
					checked={inputType === inputValg.fraEgenListe}
				>
					Egen organisasjon
				</ToggleKnapp>
				<ToggleKnapp
					key={inputValg.fraFellesListe}
					value={inputValg.fraFellesListe}
					checked={inputType === inputValg.fraFellesListe}
				>
					Felles organisasjoner
				</ToggleKnapp>
				<ToggleKnapp
					key={inputValg.skrivSelv}
					value={inputValg.skrivSelv}
					checked={inputType === inputValg.skrivSelv}
				>
					Skriv inn org.nr.
				</ToggleKnapp>
			</ToggleGruppe>
			{inputType === inputValg.fraEgenListe && (
				<EgneOrganisasjoner path={path} formikBag={formikBag} handleChange={handleChange} />
			)}
			{inputType === inputValg.fraFellesListe && (
				<OrganisasjonLoader
					render={(data) => (
						<DollySelect
							name={`${path}.orgNr`}
							label="Organisasjonsnummer"
							options={data}
							size="xlarge"
							onChange={handleChange}
							value={_get(formikBag.values, `${path}.orgNr`)}
							feil={
								_get(formikBag.values, `${path}.orgNr`) === '' && {
									feilmelding: 'Feltet er påkrevd',
								}
							}
							isClearable={false}
						/>
					)}
				/>
			)}
			{inputType === inputValg.skrivSelv && (
				<OrganisasjonTextSelect
					path={path}
					aktiveMiljoer={aktiveMiljoer}
					setEnhetsinfo={setEnhetsinfo}
					clearEnhetsinfo={clearEnhetsinfo}
				/>
			)}
		</div>
	)
}
