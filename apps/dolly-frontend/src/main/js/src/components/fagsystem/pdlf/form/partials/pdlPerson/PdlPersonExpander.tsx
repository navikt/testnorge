import React, { useEffect } from 'react'
import useBoolean from '@/utils/hooks/useBoolean'
import Button from '@/components/ui/button/Button'
import { PdlPersonForm } from '@/components/fagsystem/pdlf/form/partials/pdlPerson/PdlPersonForm'
import { NyIdent } from '@/components/fagsystem/pdlf/PdlTypes'
import { Option } from '@/service/SelectOptionsOppslag'
import { UseFormReturn } from 'react-hook-form/dist/types'

interface PdlPersonValues {
	path: string
	nyPersonPath: string
	eksisterendePersonPath: string
	fullmektigsNavnPath?: string
	eksisterendeNyPerson?: Option | null
	initialNyIdent?: any
	initialEksisterendePerson?: any
	label: string
	formMethods: UseFormReturn
	nyIdentValg?: NyIdent | null
	isExpanded?: boolean
	toggleExpansion?: boolean
}

export const PdlPersonExpander = ({
	path,
	nyPersonPath,
	eksisterendePersonPath,
	fullmektigsNavnPath,
	eksisterendeNyPerson = null,
	initialNyIdent = null,
	initialEksisterendePerson = null,
	label,
	formMethods,
	nyIdentValg = null,
	isExpanded = false,
	toggleExpansion = true,
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
			{toggleExpansion &&
				(visPersonValg ? (
					<Button onClick={setSkjulPersonValg} kind={'chevron-up'}>
						SKJUL VALG FOR {label}
					</Button>
				) : (
					<Button onClick={setVisPersonValg} kind={'chevron-down'}>
						VIS VALG FOR {label}
					</Button>
				))}
			{visPersonValg && (
				<PdlPersonForm
					path={path}
					nyPersonPath={nyPersonPath}
					eksisterendePersonPath={eksisterendePersonPath}
					fullmektigsNavnPath={fullmektigsNavnPath}
					label={label}
					formMethods={formMethods}
					nyIdentValg={nyIdentValg}
					eksisterendeNyPerson={eksisterendeNyPerson}
					initialNyIdent={initialNyIdent}
					initialEksisterendePerson={initialEksisterendePerson}
				/>
			)}
		</div>
	)
}
