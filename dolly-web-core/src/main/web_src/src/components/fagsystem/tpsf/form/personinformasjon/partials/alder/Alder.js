import React, { useState } from 'react'
import _has from 'lodash/has'
import _omit from 'lodash/omit'
import { RadioPanelGruppe } from 'nav-frontend-skjema'
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
	return _has(formikBag.values, `${basePath}.alder`) ? alderValg.alder : alderValg.spesifikk
}

export const Alder = ({ basePath, formikBag }) => {
	const [alderType, setAlderType] = useState(initialValue(basePath, formikBag))

	const paths = {
		alder: `${basePath}.alder`,
		foedtEtter: `${basePath}.foedtEtter`,
		foedtFoer: `${basePath}.foedtFoer`
	}

	const handleRadioChange = event => {
		const { value } = event.target
		setAlderType(alderValg[value])

		formikBag.setValues(_omit(formikBag.values, Object.values(paths)))

		if (value === alderValg.alder) {
			formikBag.setFieldValue(paths.alder, Formatters.randomIntInRange(1, 99))
		} else {
			formikBag.setFieldValue(paths.foedtEtter, subYears(new Date(), 80))
			formikBag.setFieldValue(paths.foedtFoer, new Date())
		}
	}

	return (
		<Vis attributt={Object.values(paths)}>
			<div className="alder-component">
				<RadioPanelGruppe
					name="alderType"
					legend="Legg til alder"
					className="myCustomTest"
					radios={[
						{ label: 'Antall år ...', value: alderValg.alder, id: alderValg.alder },
						{ label: 'Spesifikk alder ...', value: alderValg.spesifikk, id: alderValg.spesifikk }
					]}
					checked={alderType}
					onChange={handleRadioChange}
				/>

				<div>
					<FormikTextInput name={paths.alder} type="number" label="Alder" />
					<FormikDatepicker name={paths.foedtEtter} label="Født etter" />
					<FormikDatepicker name={paths.foedtFoer} label="Født før" />
				</div>
			</div>
		</Vis>
	)
}
