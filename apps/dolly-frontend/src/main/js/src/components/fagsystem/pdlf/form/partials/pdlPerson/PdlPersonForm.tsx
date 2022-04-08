import React from 'react'
import { FormikProps } from 'formik'
import { PdlNyPerson } from '~/components/fagsystem/pdlf/form/partials/pdlPerson/PdlNyPerson'
import { PdlEksisterendePerson } from '~/components/fagsystem/pdlf/form/partials/pdlPerson/PdlEksisterendePerson'
import { NyIdent } from '~/components/fagsystem/pdlf/PdlTypes'

interface PdlPersonValues {
	nyPersonPath: string
	eksisterendePersonPath: string
	label: string
	formikBag: FormikProps<{}>
	nyIdentValg?: NyIdent
}

export const PdlPersonForm = ({
	nyPersonPath,
	eksisterendePersonPath,
	label,
	formikBag,
	nyIdentValg = null,
}: PdlPersonValues) => {
	return (
		<>
			<h4>Opprett ny person</h4>
			<PdlNyPerson
				nyPersonPath={nyPersonPath}
				eksisterendePersonPath={eksisterendePersonPath}
				formikBag={formikBag}
				erNyIdent={nyIdentValg !== null}
			/>
			<h4>Velg eksisterende person</h4>
			<PdlEksisterendePerson
				nyPersonPath={nyPersonPath}
				eksisterendePersonPath={eksisterendePersonPath}
				label={label}
				formikBag={formikBag}
				nyIdentValg={nyIdentValg}
			/>
		</>
	)
}
