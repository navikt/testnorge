import React, { useEffect, useState } from 'react'
import { VelgGruppeToggle } from '@/components/velgGruppe/VelgGruppeToggle'
import { ifPresent } from '@/utils/YupValidations'
import * as _ from 'lodash'
import { DollyErrorMessage } from '@/utils/DollyErrorMessage'
import * as Yup from 'yup'
import { UseFormReturn } from 'react-hook-form/dist/types'

type VelgGruppeProps = {
	formMethods: UseFormReturn
	title: string
	fraGruppe?: number
}

export const VelgGruppe = ({
	formMethods,
	title,
	fraGruppe = null,
	gruppevalg,
	setGruppevalg,
}: VelgGruppeProps) => {
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
				gruppevalg={gruppevalg}
				setGruppevalg={setGruppevalg}
			/>
			<DollyErrorMessage name="gruppeId" />
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
