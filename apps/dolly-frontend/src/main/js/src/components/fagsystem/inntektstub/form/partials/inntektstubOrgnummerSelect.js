import React from 'react'
import { OrganisasjonLoader } from '~/components/organisasjonSelect'
import { OrgnummerToggle } from '~/components/fagsystem/inntektstub/form/partials/orgnummerToggle'

export const InntektstubOrgnummerSelect = ({ path, formikBag }) => {
	return (
		<OrgnummerToggle
			formikBag={formikBag}
			path={`${path}.virksomhet`}
			opplysningspliktigPath={`${path}.opplysningspliktig`}
		/>
	)
}
