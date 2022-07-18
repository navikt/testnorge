import React, { useEffect, useState } from 'react'
import { VelgGruppeToggle } from '~/components/velgGruppe/VelgGruppeToggle'
import { ifPresent, requiredString } from '~/utils/YupValidations'
import { FormikProps } from 'formik'
import _get from 'lodash/get'
import { ErrorMessageWithFocus } from '~/utils/ErrorMessageWithFocus'

type VelgGruppeProps = {
	formikBag: FormikProps<{}>
}

export const VelgGruppe = ({ formikBag }: VelgGruppeProps) => {
	const [valgtGruppe, setValgtGruppe] = useState(_get(formikBag.values, `gruppeId`))

	useEffect(() => setValgtGruppe(valgtGruppe || '')) // for å vise feilmeldingsvisning

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
			<ErrorMessageWithFocus name="gruppeId" className="error-message" component="div" />
		</div>
	)
}

VelgGruppe.validation = {
	gruppeId: ifPresent('$gruppeId', requiredString),
}
