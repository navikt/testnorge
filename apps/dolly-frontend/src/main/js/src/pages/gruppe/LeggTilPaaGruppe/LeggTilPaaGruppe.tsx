import Button from '@/components/ui/button/Button'
import React from 'react'
import { useNavigate } from 'react-router-dom'

export const LeggTilPaaGruppe = ({ antallPersoner, gruppeId }) => {
	const navigate = useNavigate()

	return (
		<Button
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
			title={antallPersoner < 1 ? 'Gruppen inneholder ingen personer å endre på' : null}
		>
			LEGG TIL
		</Button>
	)
}
