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

	const getButtonTitle = () => {
		if (antallPersoner < 1) {
			return 'Gruppen inneholder ingen personer å endre på'
		}
		if (antallPersoner > 50) {
			return 'Maks 50 personer kan endres i én bestilling'
		}
		return 'Legg til på alle personer i gruppen'
	}

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
			disabled={antallPersoner < 1 || antallPersoner > 50}
			title={getButtonTitle()}
		>
			LEGG TIL PÅ ALLE
		</Button>
	)
}
