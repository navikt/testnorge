import React, { useEffect, useState } from 'react'
import { VelgGruppeToggle } from '@/components/velgGruppe/VelgGruppeToggle'
import { ifPresent } from '@/utils/YupValidations'
import * as _ from 'lodash-es'
import { ErrorMessageWithFocus } from '@/utils/ErrorMessageWithFocus'
import * as Yup from 'yup'
import { UseFormReturn } from 'react-hook-form/dist/types'

type VelgGruppeProps = {
	formMethods: UseFormReturn
	title: string
	fraGruppe?: number
}

export const VelgGruppe = ({ formMethods, title, fraGruppe = null }: VelgGruppeProps) => {
	const [valgtGruppe, setValgtGruppe] = useState(_.get(formMethods.getValues(), `gruppeId`))

	useEffect(() => {
		setValgtGruppe(valgtGruppe || '') // for å vise feilmeldingsvisning
	})

	useEffect(() => {
		formMethods.setValue('gruppeId', valgtGruppe)
	}, [valgtGruppe])

	return (
		<div className="input-oppsummering">
			<h2>{title}</h2>
			<VelgGruppeToggle
				valgtGruppe={valgtGruppe}
				setValgtGruppe={setValgtGruppe}
				fraGruppe={fraGruppe}
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
