import React from 'react'
import { FormikProps } from 'formik'
import { PdlNyPerson } from '~/components/fagsystem/pdlf/form/partials/pdlPerson/PdlNyPerson'
import { PdlEksisterendePerson } from '~/components/fagsystem/pdlf/form/partials/pdlPerson/PdlEksisterendePerson'
import { NyIdent } from '~/components/fagsystem/pdlf/PdlTypes'
import { useParams } from 'react-router-dom'
import { DollyApi } from '~/service/Api'
import { useAsync } from 'react-use'
import { Option } from '~/service/SelectOptionsOppslag'

interface PdlPersonValues {
	nyPersonPath: string
	eksisterendePersonPath: string
	label: string
	formikBag: FormikProps<{}>
	nyIdentValg?: NyIdent
	eksisterendeNyPerson?: Option
}

export const PdlPersonForm = ({
	nyPersonPath,
	eksisterendePersonPath,
	label,
	formikBag,
	nyIdentValg = null,
	eksisterendeNyPerson = null,
}: PdlPersonValues) => {
	const { gruppeId } = useParams()
	const gruppe = useAsync(async () => {
		return await DollyApi.getGruppeById(gruppeId)
	}, [])
	//@ts-ignore
	const gruppeIdenter = gruppe?.value?.data?.identer?.map((person) => person.ident)

	return (
		<>
			<h4>Opprett ny person</h4>
			<PdlNyPerson
				nyPersonPath={nyPersonPath}
				eksisterendePersonPath={eksisterendePersonPath}
				formikBag={formikBag}
				erNyIdent={nyIdentValg !== null}
				gruppeIdenter={gruppeIdenter}
				eksisterendeNyPerson={eksisterendeNyPerson}
			/>
			<h4>Velg eksisterende person</h4>
			<PdlEksisterendePerson
				nyPersonPath={nyPersonPath}
				eksisterendePersonPath={eksisterendePersonPath}
				label={label}
				formikBag={formikBag}
				nyIdentValg={nyIdentValg}
				eksisterendeNyPerson={eksisterendeNyPerson}
			/>
		</>
	)
}
