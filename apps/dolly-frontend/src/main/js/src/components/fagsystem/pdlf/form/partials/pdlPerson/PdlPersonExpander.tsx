import React, { useEffect } from 'react'
import useBoolean from '~/utils/hooks/useBoolean'
import Button from '~/components/ui/button/Button'
import { FormikProps } from 'formik'
import { PdlPersonForm } from '~/components/fagsystem/pdlf/form/partials/pdlPerson/PdlPersonForm'
import { NyIdent } from '~/components/fagsystem/pdlf/PdlTypes'

interface PdlPersonValues {
	nyPersonPath: string
	eksisterendePersonPath: string
	label: string
	formikBag: FormikProps<{}>
	nyIdentValg?: NyIdent
	isExpanded?: boolean
}

export const PdlPersonExpander = ({
	nyPersonPath,
	eksisterendePersonPath,
	label,
	formikBag,
	nyIdentValg = null,
	isExpanded = false,
}: PdlPersonValues) => {
	const [visPersonValg, setVisPersonValg, setSkjulPersonValg] = useBoolean(isExpanded)
	useEffect(() => {
		if (isExpanded) {
			setVisPersonValg()
		} else {
			setSkjulPersonValg()
		}
	}, [])

	return (
		<div className="flexbox--full-width">
			{visPersonValg ? (
				<Button onClick={setSkjulPersonValg} kind={'collapse'}>
					SKJUL VALG FOR {label}
				</Button>
			) : (
				<Button onClick={setVisPersonValg} kind={'expand'}>
					VIS VALG FOR {label}
				</Button>
			)}
			{visPersonValg && (
				<>
					<PdlPersonForm
						nyPersonPath={nyPersonPath}
						eksisterendePersonPath={eksisterendePersonPath}
						label={label}
						formikBag={formikBag}
						nyIdentValg={nyIdentValg}
					/>
				</>
			)}
		</div>
	)
}
