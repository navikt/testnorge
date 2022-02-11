import React from 'react'
import useBoolean from '~/utils/hooks/useBoolean'
import Button from '~/components/ui/button/Button'
import { FormikProps } from 'formik'
import { PdlPersonForm } from '~/components/fagsystem/pdlf/form/partials/pdlPerson/PdlPersonForm'

interface PdlPersonValues {
	path: string
	label: string
	formikBag: FormikProps<{}>
	kanSettePersondata?: boolean
	erNyIdent?: boolean
}

export const PdlPersonExpander = ({
	path,
	label,
	formikBag,
	kanSettePersondata = true,
	erNyIdent = false,
}: PdlPersonValues) => {
	const [visPersonValg, setVisPersonValg, setSkjulPersonValg] = useBoolean(false)

	return (
		<div className="flexbox--full-width">
			{visPersonValg ? (
				<Button onClick={setSkjulPersonValg} kind={'collapse'}>
					SKJUL VALG FOR {label}
				</Button>
			) : (
				<Button onClick={setVisPersonValg} kind={'expand'} disabled={!kanSettePersondata}>
					VIS VALG FOR {label}
				</Button>
			)}
			{visPersonValg && (
				<div className={'flexbox--flex-wrap'} style={{ marginTop: '10px' }}>
					<PdlPersonForm path={path} formikBag={formikBag} erNyIdent={erNyIdent} />
				</div>
			)}
		</div>
	)
}
