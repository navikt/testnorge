import React, { useEffect, useState } from 'react'
import { VelgGruppeToggle } from '~/components/velgGruppe/VelgGruppeToggle'
import { ifPresent, requiredString } from '~/utils/YupValidations'
import { ErrorMessage, FormikProps } from 'formik'
import _get from 'lodash/get'

type VelgGruppeProps = {
	formikBag: FormikProps<{}>
}

export const VelgGruppe = ({ formikBag }: VelgGruppeProps) => {
	const [valgtGruppe, setValgtGruppe] = useState(_get(formikBag.values, `gruppeId`))

	useEffect(() => {
		formikBag.setFieldValue('gruppeId', valgtGruppe)
	}, [valgtGruppe])

	return (
		<div className="input-oppsummering">
			<div className="flexbox--align-center">
				<div>
					<h2>Hvilken gruppe vil du importere til?</h2>
					<VelgGruppeToggle valgtGruppe={valgtGruppe} setValgtGruppe={setValgtGruppe} />
				</div>
			</div>
			<ErrorMessage name="gruppeId" className="error-message" component="div" />
		</div>
	)
}

VelgGruppe.validation = {
	gruppeId: ifPresent('$gruppeId', requiredString),
}
