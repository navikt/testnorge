import { useNavigate } from 'react-router-dom'
import { usePdlPersonbolk } from '@/utils/hooks/usePdlPerson'
import { Button } from '@navikt/ds-react'
import { CypressSelector } from '../../../../cypress/mocks/Selectors'
import { ArrowRightIcon } from '@navikt/aksel-icons'
import React from 'react'

export const ImporterValgtePersoner = ({ identer }) => {
	const navigate = useNavigate()
	const { pdlPersoner, loading, error } = usePdlPersonbolk(identer)

	// TODO vis loading?
	const handleClick = (event) => {
		// event.stopPropagation()
		navigate(`/importer`, {
			state: {
				importPersoner: identer.map((ident) => {
					return {
						ident: ident,
						data: {
							hentPerson: pdlPersoner?.hentPersonBolk?.find((p) => p.ident === ident)?.person,
							hentIdenter: pdlPersoner?.hentIdenterBolk?.find((p) => p.ident === ident)?.identer,
						},
					}
				}),
				// mal: mal,
				// gruppe: gruppe,
			},
		})
	}

	return (
		<Button
			variant="primary"
			size="small"
			disabled={identer?.length < 1}
			loading={loading}
			onClick={handleClick}
		>
			Importer {identer?.length} valgte personer
		</Button>
	)
}
