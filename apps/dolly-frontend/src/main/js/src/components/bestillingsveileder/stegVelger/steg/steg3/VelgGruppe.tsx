import React, { useEffect, useState } from 'react'
import { VelgGruppeToggle } from '@/components/velgGruppe/VelgGruppeToggle'
import { ifPresent } from '@/utils/YupValidations'
import { FormikProps } from 'formik'
import * as _ from 'lodash-es'
import { ErrorMessageWithFocus } from '@/utils/ErrorMessageWithFocus'
import * as Yup from 'yup'

type VelgGruppeProps = {
	formikBag: FormikProps<{}>
	title: string
	fraGruppe?: number
}

export const VelgGruppe = ({
	formikBag,
	title,
	fraGruppe = null,
	gruppevalg,
	setGruppevalg,
}: VelgGruppeProps) => {
	const [valgtGruppe, setValgtGruppe] = useState(_.get(formikBag.values, `gruppeId`))

	useEffect(() => {
		setValgtGruppe(valgtGruppe || '') // for å vise feilmeldingsvisning
	})

	useEffect(() => {
		formikBag.setFieldValue('gruppeId', valgtGruppe)
	}, [valgtGruppe])

	return (
		<div className="input-oppsummering">
			<h2>{title}</h2>
			<VelgGruppeToggle
				valgtGruppe={valgtGruppe}
				setValgtGruppe={setValgtGruppe}
				fraGruppe={fraGruppe}
				gruppevalg={gruppevalg}
				setGruppevalg={setGruppevalg}
			/>
			<ErrorMessageWithFocus name="gruppeId" className="error-message" component="div" />
		</div>
	)
}

VelgGruppe.validation = {
	gruppeId: ifPresent(
		'$gruppeId',
		Yup.string().required(
			'Velg eksisterende gruppe eller opprett ny gruppe for å importere personen(e)',
		),
	),
}
