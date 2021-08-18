import React, { useState } from 'react'
import _has from 'lodash/has'
import _get from 'lodash/get'
import _omit from 'lodash/omit'
import { ToggleGruppe, ToggleKnapp } from 'nav-frontend-skjema'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { subYears } from 'date-fns'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import Formatters from '~/utils/DataFormatter'

import './alder.less'

const alderValg = {
	alder: 'alder',
	spesifikk: 'spesifikk'
}

const initialValue = (basePath, formikBag) => {
	return _has(formikBag.values, `${basePath}.alder`)
		? alderValg.alder
		: _has(formikBag.values, `${basePath}.foedtEtter`)
		? alderValg.spesifikk
		: null
}

export const Alder = ({ basePath, formikBag, title, handleDoed }) => {
	const [alderType, setAlderType] = useState(initialValue(basePath, formikBag))
	const paths = {
		alder: `${basePath}.alder`,
		foedtEtter: `${basePath}.foedtEtter`,
		foedtFoer: `${basePath}.foedtFoer`,
		doedsdato: `${basePath}.doedsdato`
	}

	const handleToggleChange = event => {
		const { value } = event.target
		setAlderType(alderValg[value])

		const barn = basePath.includes('barn')
		const forelder = basePath.includes('foreldre')

		formikBag.setValues(_omit(formikBag.values, Object.values(paths)))
		formikBag.setFieldValue(paths.doedsdato, _get(formikBag.values, `${basePath}.doedsdato`))

		if (value === alderValg.alder) {
			if (barn) {
				formikBag.setFieldValue(paths.alder, Formatters.randomIntInRange(0, 17))
			} else if (forelder) {
				formikBag.setFieldValue(paths.alder, Formatters.randomIntInRange(65, 100))
			} else {
				formikBag.setFieldValue(paths.alder, Formatters.randomIntInRange(30, 60))
			}
		} else {
			if (barn) {
				formikBag.setFieldValue(paths.foedtEtter, subYears(new Date(), 17))
				formikBag.setFieldValue(paths.foedtFoer, new Date())
			} else if (forelder) {
				formikBag.setFieldValue(paths.foedtEtter, subYears(new Date(), 100))
				formikBag.setFieldValue(paths.foedtFoer, subYears(new Date(), 65))
			} else {
				formikBag.setFieldValue(paths.foedtEtter, subYears(new Date(), 60))
				formikBag.setFieldValue(paths.foedtFoer, subYears(new Date(), 30))
			}
		}
	}

	const toggleValues = [
		{
			value: alderValg.alder,
			label: 'Antall år ...'
		},
		{
			value: alderValg.spesifikk,
			label: 'Født før/etter ...'
		}
	]

	return (
		<Vis attributt={Object.values(paths)}>
			<div className="alder-component">
				{title && <h4>{title}</h4>}
				{alderType && (
					<ToggleGruppe onChange={handleToggleChange} name={paths.alder}>
						{toggleValues.map(val => (
							<ToggleKnapp key={val.value} value={val.value} checked={alderType === val.value}>
								{val.label}
							</ToggleKnapp>
						))}
					</ToggleGruppe>
				)}

				<div className="alder-felter">
					<FormikTextInput name={paths.alder} type="number" label="Antall år" />
					<FormikDatepicker name={paths.foedtEtter} label="Født etter" />
					<FormikDatepicker name={paths.foedtFoer} label="Født før" />
					<Vis attributt={paths.doedsdato}>
						{handleDoed ? (
							<FormikDatepicker name={paths.doedsdato} label="Dødsdato" onChange={handleDoed} />
						) : (
							<FormikDatepicker name={paths.doedsdato} label="Dødsdato" />
						)}
					</Vis>
				</div>
			</div>
		</Vis>
	)
}
