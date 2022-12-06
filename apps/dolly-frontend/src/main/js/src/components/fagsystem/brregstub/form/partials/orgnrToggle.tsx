import React, { useState } from 'react'
import _get from 'lodash/get'
import { FormikProps } from 'formik'
import { OrganisasjonTextSelect } from '@/components/fagsystem/brregstub/form/partials/organisasjonTextSelect'
import {
	inputValg,
	OrganisasjonToogleGruppe,
} from '@/components/organisasjonSelect/OrganisasjonToogleGruppe'
import { EgneOrganisasjoner } from '@/components/fagsystem/brregstub/form/partials/EgneOrganisasjoner'
import { useDollyEnvironments } from '@/utils/hooks/useEnvironments'
import { OrganisasjonLoader } from '@/components/organisasjonSelect/OrganisasjonLoader'

interface OrgnrToggleProps {
	path: string
	formikBag: FormikProps<any>
	setEnhetsinfo: (org: any, path: string) => Record<string, unknown>
	warningMessage?: any
}

export const OrgnrToggle = ({
	path,
	formikBag,
	setEnhetsinfo,
	warningMessage,
}: OrgnrToggleProps) => {
	const [inputType, setInputType] = useState(inputValg.fraFellesListe)
	const { dollyEnvironments: aktiveMiljoer } = useDollyEnvironments()

	const handleToggleChange = (value: string) => {
		setInputType(value)
		clearEnhetsinfo()
	}

	const clearEnhetsinfo = () => {
		const oldValues = _get(formikBag.values, path) || {}
		if (oldValues.hasOwnProperty('foretaksNavn')) {
			delete oldValues['foretaksNavn']
		}
		if (oldValues.hasOwnProperty('forretningsAdresse')) {
			delete oldValues['forretningsAdresse']
		}
		if (oldValues.hasOwnProperty('postAdresse')) {
			delete oldValues['postAdresse']
		}
		oldValues['orgNr'] = ''
		formikBag.setFieldValue(path, oldValues)
	}

	const handleChange = (event: React.ChangeEvent<any>) => {
		setEnhetsinfo(event, path)
	}

	return (
		<div className="toggle--wrapper">
			<OrganisasjonToogleGruppe inputType={inputType} handleToggleChange={handleToggleChange} />
			{inputType === inputValg.fraFellesListe && (
				<OrganisasjonLoader
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
				<EgneOrganisasjoner
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
