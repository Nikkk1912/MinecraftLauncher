declare module '*.svg' {
    import { FunctionComponent, SVGProps } from "react";
    const ReactComponent: FunctionComponent<React.SVGProps<SVGElement>>;
    export { ReactComponent };
}

// declare module '*.svg' {
//     const content: React.FC<React.SVGProps<SVGElement>>
//     export default content
// }