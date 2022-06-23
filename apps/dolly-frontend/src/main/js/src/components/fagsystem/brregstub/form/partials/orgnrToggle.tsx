import React, { useState } from 'react'
import _get from 'lodash/get'
import { FormikProps } from 'formik'
import { OrganisasjonTextSelect } from '~/components/fagsystem/brregstub/form/partials/organisasjonTextSelect'
import {
	inputValg,
	OrganisasjonToogleGruppe,
} from '~/components/organisasjonSelect/OrganisasjonToogleGruppe'
import OrganisasjonLoaderConnector from '~/components/organisasjonSelect/OrganisasjonLoaderConnector'
import EgneOrganisasjonerConnector from '~/components/fagsystem/brregstub/form/partials/EgneOrganisasjonerConnector'
import { AlertStripeAdvarsel } from 'nav-frontend-alertstriper'
import { useDollyEnvironments } from '~/utils/hooks/useEnvironments'

interface OrgnrToggleProps {
	path: string
	formikBag: FormikProps<{}>
	setEnhetsinfo: (org: any, path: string) => {}
	warningMessage?: AlertStripeAdvarsel
}

export const OrgnrToggle = ({
	path,
	formikBag,
	setEnhetsinfo,
	warningMessage,
}: OrgnrToggleProps) => {
	const [inputType, setInputType] = useState(inputValg.fraFellesListe)
	const { dollyEnvironments: aktiveMiljoer } = useDollyEnvironments()

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
				<EgneOrganisasjonerConnector
					path={`${path}.orgNr`}
					formikBag={formikBag}
					handleChange={handleChange}
					warningMessage={warningMessage}
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
