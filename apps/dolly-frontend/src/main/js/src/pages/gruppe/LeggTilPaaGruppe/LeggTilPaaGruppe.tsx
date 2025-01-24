import Button from '@/components/ui/button/Button'
import React from 'react'
import { useNavigate } from 'react-router'
import { TestComponentSelectors } from '#/mocks/Selectors'

type LeggTilProps = {
	antallPersoner: number
	gruppeId: string
}

export const LeggTilPaaGruppe = ({ antallPersoner, gruppeId }: LeggTilProps) => {
	const navigate = useNavigate()

	return (
		<Button
			data-testid={TestComponentSelectors.BUTTON_LEGGTILPAAALLE}
			onClick={() =>
				navigate(`/gruppe/${gruppeId}/bestilling/`, {
					state: {
						antall: antallPersoner,
						leggTilPaaGruppe: gruppeId,
					},
				})
			}
			kind="add-circle"
			disabled={antallPersoner < 1}
			title={antallPersoner < 1 ? 'Gruppen inneholder ingen personer å endre på' : undefined}
		>
			LEGG TIL PÅ ALLE
		</Button>
	)
}
