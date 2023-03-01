import _get from 'lodash/get'
import { Fritekstfelt } from '@/components/fagsystem/arbeidsplassen/form/styles'
import * as React from 'react'

export const SammendragForm = ({ formikBag }) => {
	return (
		<>
			<h3>Sammendrag</h3>
			<Fritekstfelt
				label="Oppsummering"
				placeholder="Kort oppsummering av kompetanse og personlige egenskaper"
				value={_get(formikBag.values, 'arbeidsplassenCV.sammendrag')}
				onChange={(sammendrag) =>
					formikBag.setFieldValue('arbeidsplassenCV.sammendrag', sammendrag?.target?.value)
				}
			/>
		</>
	)
}
