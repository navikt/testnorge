import React from 'react'
import { OrganisasjonLoader } from '~/components/organisasjonSelect'
import { OrgnummerToggle } from '~/components/fagsystem/aareg/form/partials/orgnummerToggle'

export const InntektstubOrgnummerSelect = ({ path, formikBag }) => {
	return (
		<OrganisasjonLoader
			kanHaArbeidsforhold={true}
			render={() => (
				<OrgnummerToggle
					formikBag={formikBag}
					path={`${path}.virksomhet`}
					opplysningspliktigPath={`${path}.opplysningspliktig`}
				/>
			)}
		/>
	)
}
