import React, { useState } from 'react'
import { VelgGruppeToggle } from '~/components/velgGruppe/VelgGruppeToggle'
import { ifPresent, requiredString } from '~/utils/YupValidations'
import { FormikProps } from 'formik'

type VelgGruppeProps = {
	formikBag: FormikProps<{}>
}

export const VelgGruppe = ({ formikBag }: VelgGruppeProps) => {
	const getInitialValue = () => {
		formikBag.setFieldValue('gruppeId', undefined)
		return undefined
	}
	const [valgtGruppe, setValgtGruppe] = useState(getInitialValue)

	const setGruppe = (gruppe: string) => {
		setValgtGruppe(gruppe)
		formikBag.setFieldValue('gruppeId', gruppe)
	}

	return (
		<div className="input-oppsummering">
			<div className="flexbox--align-center">
				<div>
					<h2>Hvilken gruppe vil du importere til?</h2>
					<VelgGruppeToggle valgtGruppe={valgtGruppe} setValgtGruppe={setGruppe} />
				</div>
			</div>
		</div>
	)
}

VelgGruppe.validation = {
	gruppeId: ifPresent('$gruppeId', requiredString),
}
