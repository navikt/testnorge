import React from 'react'
import { PoppVisningMiljoeinfo } from '~/components/fagsystem/pensjon/visning/PoppVisningMiljoeinfo'
import { useAsync } from 'react-use'
import { DollyApi } from '~/service/Api'
import Loading from '~/components/ui/loading/Loading'
import styled from 'styled-components'

type PoppMiljoeinfoTypes = {
	ident: string
	bestilteMiljoer: Array<string>
}

const Miljoeinfo = styled.div`
	margin-bottom: 10px;
	display: flex;
	flex-wrap: wrap;

	p {
		margin: 0;
	}
`

export const PoppMiljoeinfo = ({ ident, bestilteMiljoer }: PoppMiljoeinfoTypes) => {
	const miljoer = useAsync(async () => {
		return DollyApi.getPoppMiljoer()
	}, [])

	return (
		<div className="flexbox--flex-wrap" style={{ marginTop: '15px' }}>
			{miljoer && !miljoer?.loading ? (
				<Miljoeinfo>
					<PoppVisningMiljoeinfo
						//@ts-ignore
						miljoer={miljoer?.value?.data}
						ident={ident}
						bestilteMiljoer={bestilteMiljoer}
					/>
					<p>
						<i>
							Hold pekeren over et miljø for å se dataene som finnes på denne personen i POPP for
							det aktuelle miljøet.
						</i>
					</p>
				</Miljoeinfo>
			) : (
				<Loading label={'Laster miljøer...'} />
			)}
		</div>
	)
}
