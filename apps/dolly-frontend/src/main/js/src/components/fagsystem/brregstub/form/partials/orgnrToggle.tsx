import React, { useEffect, useState } from 'react'
import _get from 'lodash/get'
import { FormikProps } from 'formik'
import { EgneOrganisasjoner } from '~/components/fagsystem/brregstub/form/partials/egneOrganisasjoner'
import { OrganisasjonTextSelect } from '~/components/fagsystem/brregstub/form/partials/organisasjonTextSelect'
import { MiljoeApi } from '~/service/Api'
import {
	inputValg,
	OrganisasjonToogleGruppe,
} from '~/components/organisasjonSelect/OrganisasjonToogleGruppe'
import OrganisasjonLoaderConnector from '~/components/organisasjonSelect/OrganisasjonLoaderConnector'

interface OrgnrToggleProps {
	path: string
	formikBag: FormikProps<{}>
	setEnhetsinfo: (org: any, path: string) => {}
}

export const OrgnrToggle = ({ path, formikBag, setEnhetsinfo }: OrgnrToggleProps) => {
	const [inputType, setInputType] = useState(inputValg.fraFellesListe)
	const [aktiveMiljoer, setAktiveMiljoer] = useState(null)

	useEffect(() => {
		const fetchData = async () => {
			setAktiveMiljoer(await MiljoeApi.getAktiveMiljoer())
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
			<OrganisasjonToogleGruppe
				path={path}
				inputType={inputType}
				handleToggleChange={handleToggleChange}
			/>
			{inputType === inputValg.fraFellesListe && (
				<OrganisasjonLoaderConnector
					path={`${path}.orgNr`}
					handleChange={handleChange}
					value={_get(formikBag.values, `${path}.orgNr`)}
					feil={
						_get(formikBag.values, path) === '' && {
							feilmelding: 'Feltet er påkrevd',
						}
					}
				/>
			)}
			{inputType === inputValg.fraEgenListe && (
				<EgneOrganisasjoner path={path} formikBag={formikBag} handleChange={handleChange} />
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
