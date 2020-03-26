import * as React from 'react';
// @ts-ignore
import {v4 as uuid} from 'uuid';
import NavHjelpeTekst from 'nav-frontend-hjelpetekst'
import Logger from '../../logger';

interface Props {
    hjelpetekstFor: string,
    children: React.ReactNode
}

export class Hjelpetekst extends React.Component<Props> {
    uuid: string;

    constructor(props: Props) {
        super(props);
        this.uuid = uuid();
    }

    render() {
        const {children, hjelpetekstFor} = this.props;
        const onClick = (e: React.MouseEvent<HTMLDivElement>): void => {
            e.stopPropagation();
            Logger.log(`Trykk på hjelpetekst for: ${hjelpetekstFor}`, null, this.uuid)
        };

        return (
            <div onClick={onClick}>
                <NavHjelpeTekst>
                    {children}
                </NavHjelpeTekst>
            </div>
        )
    }
}