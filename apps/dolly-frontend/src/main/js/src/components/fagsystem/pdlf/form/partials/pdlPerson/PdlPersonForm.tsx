import React from 'react'
import { FormikProps } from 'formik'
import { PdlNyPerson } from '~/components/fagsystem/pdlf/form/partials/pdlPerson/PdlNyPerson'
import { PdlEksisterendePerson } from '~/components/fagsystem/pdlf/form/partials/pdlPerson/PdlEksisterendePerson'

interface PdlPersonValues {
	nyPersonPath: string
	eksisterendePersonPath: string
	label: string
	formikBag: FormikProps<{}>
	erNyIdent?: boolean
}

export const PdlPersonForm = ({
	nyPersonPath,
	eksisterendePersonPath,
	label,
	formikBag,
	erNyIdent = false,
}: PdlPersonValues) => {
	return (
		<>
			<h4>Opprett ny person</h4>
			<PdlNyPerson
				nyPersonPath={nyPersonPath}
				eksisterendePersonPath={eksisterendePersonPath}
				formikBag={formikBag}
				erNyIdent={erNyIdent}
			/>
			<h4>Velg eksisterende person</h4>
			<PdlEksisterendePerson
				nyPersonPath={nyPersonPath}
				eksisterendePersonPath={eksisterendePersonPath}
				label={label}
				formikBag={formikBag}
			/>
		</>
	)
}
